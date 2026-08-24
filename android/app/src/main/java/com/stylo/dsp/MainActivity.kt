package com.stylo.dsp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.*
import android.media.audiofx.Equalizer
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import java.util.Locale
import kotlin.math.*

class MainActivity : Activity() {
    private val model = EqModel()
    private lateinit var graph: EqView
    private var recorder: AudioRecord? = null
    private var audioThread: Thread? = null
    @Volatile private var running = false
    private var player: MediaPlayer? = null
    private var playerEq: Equalizer? = null
    private var seek: SeekBar? = null
    private var timeText: TextView? = null
    private var titleText: TextView? = null

    override fun onCreate(state: Bundle?) { super.onCreate(state); buildUi(); if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED) requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),10) else startAnalyzer() }

    private fun buildUi() {
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setBackgroundColor(0xff090a0c.toInt())}
        graph=EqView();root.addView(graph,LinearLayout.LayoutParams(-1,0,1f))
        titleText=TextView(this).apply{text="Sin archivo · STYLO EQ";setTextColor(0xffb8c3c8.toInt());textSize=13f;setPadding(18,2,18,0)};root.addView(titleText)
        seek=SeekBar(this).apply{max=1000;setPadding(18,0,18,0);setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{override fun onProgressChanged(s:SeekBar?,p:Int,from:Boolean){if(from)player?.let{if(it.duration>0)it.seekTo(it.duration*p/1000);updateTime()}};override fun onStartTrackingTouch(s:SeekBar?){};override fun onStopTrackingTouch(s:SeekBar?){} })};root.addView(seek,LinearLayout.LayoutParams(-1,42))
        timeText=TextView(this).apply{text="00:00 / 00:00";gravity=Gravity.CENTER;setTextColor(0xffd9e4e8.toInt());textSize=12f};root.addView(timeText,LinearLayout.LayoutParams(-1,25))
        val row=LinearLayout(this).apply{gravity=Gravity.CENTER};fun b(t:String,a:()->Unit)=Button(this).apply{text=t;setOnClickListener{a()}}
        row.addView(b("📂"){openAudio()});row.addView(b("⏮"){player?.seekTo(max(0,(player?.currentPosition?:0)-10000))});row.addView(b("▶/⏸"){togglePlay()});row.addView(b("⏭"){player?.seekTo(min(player?.duration?:0,(player?.currentPosition?:0)+10000))});row.addView(b("A/B"){model.ab=!model.ab;graph.invalidate();applyEq()});root.addView(row,LinearLayout.LayoutParams(-1,52))
        val eq=LinearLayout(this).apply{gravity=Gravity.CENTER};eq.addView(b("31-BAND"){model.parametricMode=false;model.selected=15;graph.invalidate();applyEq()});eq.addView(b("PARAM 8"){model.parametricMode=true;model.selected=0;graph.invalidate();applyEq()});eq.addView(b("BYPASS"){model.bypass=!model.bypass;graph.invalidate();applyEq()});eq.addView(b("RESET"){model.active.forEach{it.gain=0f};graph.invalidate();applyEq()});root.addView(eq,LinearLayout.LayoutParams(-1,52));setContentView(root)
    }
    private fun openAudio(){startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="audio/*";addCategory(Intent.CATEGORY_OPENABLE)},42)}
    override fun onActivityResult(r:Int,c:Int,d:Intent?){super.onActivityResult(r,c,d);if(r==42&&c==RESULT_OK)d?.data?.let{playUri(it)}}
    private fun playUri(uri:Uri){releasePlayer();player=MediaPlayer.create(this,uri);player?.setOnCompletionListener{seek?.progress=1000};titleText?.text="STYLO EQ · ${uri.lastPathSegment?:"audio"}";setupEq();player?.start();updateTime()}
    private fun setupEq(){try{playerEq=Equalizer(0,player?.audioSessionId?:0);playerEq?.enabled=true;applyEq()}catch(_:Throwable){}}
    private fun applyEq(){val eq=playerEq?:return;val n=eq.numberOfBands.toInt();val lo=eq.bandLevelRange[0].toInt()/100f;val hi=eq.bandLevelRange[1].toInt()/100f;for(i in 0 until n){val f=eq.getCenterFreq(i.toShort())/1000.0;val band=model.active.minByOrNull{abs(ln(max(20.0,f)/it.freq))};val g=(band?.gain?:0f).coerceIn(lo,hi);try{eq.setBandLevel(i.toShort(),(g*100).toInt().toShort())}catch(_:Throwable){}}}
    private fun togglePlay(){player?.let{if(it.isPlaying)it.pause()else it.start();updateTime()}}
    private fun updateTime(){val p=player?:return;val cur=p.currentPosition;val dur=max(1,p.duration);seek?.progress=(cur*1000/dur).coerceIn(0,1000);timeText?.text="${fmt(cur)} / ${fmt(dur)}";seek?.postDelayed({if(player?.isPlaying==true)updateTime()},250)}
    private fun fmt(ms:Int)=String.format(Locale.US,"%02d:%02d",ms/60000,(ms/1000)%60)
    private fun startAnalyzer(){if(running)return;val rate=44100;val n=2048;val minBuf=AudioRecord.getMinBufferSize(rate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);if(minBuf<=0)return;recorder=AudioRecord(MediaRecorder.AudioSource.DEFAULT,rate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,max(minBuf,n*2));try{recorder?.startRecording()}catch(_:Throwable){return};running=true;audioThread=Thread{Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);val samples=ShortArray(n);val mags=FloatArray(n/2);val fft=FastFft(n);while(running){val got=recorder?.read(samples,0,n)?:0;if(got>0){fft.magnitudeDb(samples,got,mags);graph.post{if(running)graph.setSpectrum(mags)}}}}.also{it.start()}}
    override fun onRequestPermissionsResult(r:Int,p:Array<out String>,g:IntArray){super.onRequestPermissionsResult(r,p,g);if(r==10&&g.firstOrNull()==PackageManager.PERMISSION_GRANTED)startAnalyzer()}
    override fun onDestroy(){running=false;try{recorder?.stop()}catch(_:Throwable){};recorder?.release();audioThread?.join(300);releasePlayer();super.onDestroy()}
    private fun releasePlayer(){try{player?.stop()}catch(_:Throwable){};player?.release();player=null;playerEq?.release();playerEq=null}

    private inner class EqView:View(this){private val p=Paint(Paint.ANTI_ALIAS_FLAG);private val path=Path();private val spectrum=FloatArray(1024);private var drag=false
        fun setSpectrum(v:FloatArray){System.arraycopy(v,0,spectrum,0,min(v.size,spectrum.size));invalidate()}
        private fun xToF(x:Float)=20.0*exp(ln(1000.0)*x/max(1f,width.toFloat()));private fun fToX(f:Double)=(ln(f/20.0)/ln(1000.0)*width).toFloat();private fun yToG(y:Float)=12f-24f*(y-height*.08f)/(height*.80f);private fun gToY(g:Double)=(height*.88f-(g+12.0)/24.0*height*.80f).toFloat()
        override fun onDraw(c:Canvas){c.drawColor(0xff090a0c.toInt());val w=width.toFloat();val h=height.toFloat();p.style=Paint.Style.STROKE;p.strokeWidth=1f;p.color=0x30363c42;for(i in 0..10)c.drawLine(0f,h*.08f+i*h*.80f/10,w,h*.08f+i*h*.80f/10,p);for(f in doubleArrayOf(20.0,50.0,100.0,500.0,1000.0,5000.0,10000.0,20000.0)){val x=fToX(f);c.drawLine(x,h*.06f,x,h*.90f,p)}
            p.color=0xaa67ddf2.toInt();p.strokeWidth=1.5f;path.reset();var started=false;for(i in 1 until spectrum.size){val f=i.toDouble()/spectrum.size*22050;if(f<20||f>20000)continue;val x=fToX(f);val db=spectrum[i].coerceIn(-80f,0f);val y=h*.88f-(db+80f)/80f*h*.76f;if(!started){path.moveTo(x,y);started=true}else path.lineTo(x,y)};c.drawPath(path,p)
            p.color=0xffe9f4f7.toInt();p.strokeWidth=3f;path.reset();for(x in 0..w.toInt() step 4){val y=gToY(model.responseDb(xToF(x.toFloat())));if(x==0)path.moveTo(0f,y)else path.lineTo(x.toFloat(),y)};c.drawPath(path,p);p.style=Paint.Style.FILL;model.active.forEachIndexed{idx,b->p.color=if(idx==model.selected)0xff7de7ff.toInt()else 0xff789099.toInt();c.drawCircle(fToX(b.freq),gToY(b.gain.toDouble()),if(idx==model.selected)9f else 4f,p)}
            p.color=0xffb8c3c8.toInt();p.textSize=24f;c.drawText("STYLO EQ",20f,30f,p);p.textSize=13f;val mode=if(model.parametricMode)"PARAMETRIC · 8 BANDS" else "GRAPHIC · 31 BANDS";c.drawText(mode,20f,49f,p);val b=model.active[model.selected];c.drawText("${b.freq.roundToInt()} Hz   ${"%+.1f".format(b.gain)} dB   Q ${"%.2f".format(b.q)}",20f,h-14f,p)}
        override fun onTouchEvent(e:MotionEvent):Boolean{val f=xToF(e.x);when(e.actionMasked){MotionEvent.ACTION_DOWN->{model.selected=model.nearestBand(f);val b=model.active[model.selected];drag=abs(e.x-fToX(b.freq))<40&&abs(e.y-gToY(b.gain.toDouble()))<40;invalidate();return true};MotionEvent.ACTION_MOVE->if(drag){val b=model.active[model.selected];b.freq=xToF(e.x).coerceIn(20.0,20000.0);b.gain=yToG(e.y).coerceIn(-12f,12f);invalidate();applyEq();return true};MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL->{drag=false;return true}};return true}
    }
}
