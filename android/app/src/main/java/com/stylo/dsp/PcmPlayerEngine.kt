package com.stylo.dsp

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Handler
import android.os.Looper
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

/** Production path: MediaExtractor -> MediaCodec -> PCM -> custom EQ -> FFT -> AudioTrack. */
class PcmPlayerEngine(private val context: Context, private val onState: (Int, Int, Boolean) -> Unit, private val onSpectrum: ((FloatArray) -> Unit)? = null) {
    private val handler=Handler(Looper.getMainLooper())
    private val playing=AtomicBoolean(false)
    private val stopRequested=AtomicBoolean(false)
    @Volatile private var seekRequestMs:Int?=null
    @Volatile private var volume=1f
    @Volatile private var loop=false
    @Volatile private var bypass=false
    @Volatile private var bands:List<EqBand> = emptyList()
    @Volatile private var bandsVersion=0L
    private var worker:Thread?=null
    private var uri:Uri?=null
    @Volatile private var positionMs=0
    @Volatile private var durationMs=0
    @Volatile private var sampleRate=44100
    @Volatile private var channels=1
    private var audioTrack:AudioTrack?=null

    fun load(newUri:Uri){stop();uri=newUri;positionMs=0;durationMs=0;startWorker()}
    fun setBands(newBands:List<EqBand>){bands=newBands.map{it.copy()};bandsVersion++}
    fun setBypass(value:Boolean){bypass=value}
    fun setVolume(v:Float){volume=v.coerceIn(0f,1f);audioTrack?.setVolume(volume)}
    fun setLoop(v:Boolean){loop=v}
    fun isLoaded()=uri!=null
    fun currentPosition()=positionMs
    fun duration()=durationMs
    fun playPause(){if(uri==null)return;if(playing.get()){playing.set(false);audioTrack?.pause()}else{playing.set(true);audioTrack?.play();if(worker?.isAlive!=true)startWorker()};emitState()}
    fun seekBy(deltaMs:Int)=seekTo((positionMs+deltaMs).coerceIn(0,durationMs.coerceAtLeast(0)))
    fun seekTo(ms:Int){seekRequestMs=ms.coerceIn(0,durationMs.coerceAtLeast(0));if(worker?.isAlive!=true)startWorker();emitState()}
    private fun startWorker(){stopRequested.set(false);worker=Thread{decodeLoop()}.also{it.start()}}

    private fun decodeLoop(){
        val source=uri?:return
        val extractor=MediaExtractor();var codec:MediaCodec?=null
        try{
            extractor.setDataSource(context,source,null)
            var trackIndex=-1
            for(i in 0 until extractor.trackCount){val f=extractor.getTrackFormat(i);if(f.getString(MediaFormat.KEY_MIME)?.startsWith("audio/")==true){trackIndex=i;break}}
            if(trackIndex<0)return
            extractor.selectTrack(trackIndex);val format=extractor.getTrackFormat(trackIndex);val mime=format.getString(MediaFormat.KEY_MIME)?:return
            sampleRate=format.getInteger(MediaFormat.KEY_SAMPLE_RATE);channels=format.getInteger(MediaFormat.KEY_CHANNEL_COUNT).coerceIn(1,2);durationMs=if(format.containsKey(MediaFormat.KEY_DURATION))(format.getLong(MediaFormat.KEY_DURATION)/1000L).toInt()else 0
            codec=MediaCodec.createDecoderByType(mime);codec.configure(format,null,null,0);codec.start()
            val mask=if(channels==1)AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO
            val minBuf=AudioTrack.getMinBufferSize(sampleRate,mask,AudioFormat.ENCODING_PCM_16BIT);require(minBuf>0){"Invalid AudioTrack buffer size: $minBuf"}
            audioTrack=AudioTrack.Builder().setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()).setAudioFormat(AudioFormat.Builder().setSampleRate(sampleRate).setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(mask).build()).setBufferSizeInBytes(max(minBuf,sampleRate*channels/4*2)).setTransferMode(AudioTrack.MODE_STREAM).build().also{it.setVolume(volume)}
            val processors=Array(channels){EqBank(sampleRate)}
            var appliedVersion=-1L;var inputDone=false;var outputDone=false;var scratch=ShortArray(4096)
            val fft=if(onSpectrum!=null)Fft(2048)else null;val fftInput=if(onSpectrum!=null)ShortArray(2048)else null;val mags=if(onSpectrum!=null)FloatArray(1024)else null
            var fftFill=0;var lastSpectrumNs=0L;val limiter=SafetyLimiter();val info=MediaCodec.BufferInfo()
            while(!stopRequested.get()&&!outputDone){
                seekRequestMs?.let{target->seekRequestMs=null;extractor.seekTo(target*1000L,MediaExtractor.SEEK_TO_CLOSEST_SYNC);codec.flush();inputDone=false;outputDone=false;positionMs=target;processors.forEach{it.reset()};audioTrack?.pause();audioTrack?.flush();if(playing.get())audioTrack?.play();fftFill=0;emitState()}
                val version=bandsVersion
                if(appliedVersion!=version){val snapshot=bands;processors.forEach{bank->bank.setBands(snapshot);bank.reset()};appliedVersion=version}
                if(!inputDone){val inputIndex=codec.dequeueInputBuffer(10_000);if(inputIndex>=0){val input=codec.getInputBuffer(inputIndex)?:continue;input.clear();val size=extractor.readSampleData(input,0);if(size<0){codec.queueInputBuffer(inputIndex,0,0,0L,MediaCodec.BUFFER_FLAG_END_OF_STREAM);inputDone=true}else{codec.queueInputBuffer(inputIndex,0,size,extractor.sampleTime,0);extractor.advance()}}}
                val outIndex=codec.dequeueOutputBuffer(info,10_000)
                if(outIndex>=0){val out=codec.getOutputBuffer(outIndex);if(out!=null&&info.size>0){val sampleCount=info.size/2;if(scratch.size<sampleCount)scratch=ShortArray(sampleCount);val view=out.duplicate().order(ByteOrder.LITTLE_ENDIAN);view.position(info.offset);view.limit(info.offset+info.size);view.asShortBuffer().get(scratch,0,sampleCount)
                        if(!bypass)for(i in 0 until sampleCount){val ch=i%channels;val x=scratch[i]/32768.0;scratch[i]=(limiter.process(processors[ch].process(x)).coerceIn(-1.0,1.0)*32767.0).toInt().toShort()}
                        if(fft!=null&&fftInput!=null&&mags!=null){var i=0;while(i<sampleCount&&fftFill<fftInput.size){val mono=if(channels==1)scratch[i] else ((scratch[i].toInt()+scratch.getOrElse(i+1){scratch[i].toInt()})/2).toShort();fftInput[fftFill++]=mono;i+=channels};val now=System.nanoTime();if(fftFill==fftInput.size&&now-lastSpectrumNs>=33_000_000L){fft.process(fftInput,fftInput.size,mags);val snapshot=mags.copyOf();handler.post{onSpectrum?.invoke(snapshot)};lastSpectrumNs=now;fftFill=0}else if(fftFill==fftInput.size)fftFill=0}
                        audioTrack?.write(scratch,0,sampleCount,AudioTrack.WRITE_BLOCKING);positionMs=(info.presentationTimeUs/1000L).toInt();emitState()}
                    codec.releaseOutputBuffer(outIndex,false);if(info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM!=0){if(loop&&!stopRequested.get()){extractor.seekTo(0L,MediaExtractor.SEEK_TO_CLOSEST_SYNC);codec.flush();inputDone=false;outputDone=false;positionMs=0;processors.forEach{it.reset()};audioTrack?.pause();audioTrack?.flush();if(playing.get())audioTrack?.play();fftFill=0;emitState()}else{playing.set(false);outputDone=true}}}
                if(playing.get())audioTrack?.play()else audioTrack?.pause()
            }
        }catch(_:Throwable){playing.set(false)}finally{runCatching{codec?.stop()};runCatching{codec?.release()};runCatching{extractor.release()};runCatching{audioTrack?.stop()};runCatching{audioTrack?.release()};audioTrack=null;emitState()}
    }
    private fun emitState(){handler.post{onState(positionMs,durationMs,playing.get())}}
    fun stop(){stopRequested.set(true);playing.set(false);worker?.join(500);worker=null;runCatching{audioTrack?.release()};audioTrack=null}
    fun release(){stop();uri=null}
}
