package com.stylo.eq

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : Activity() {
    private lateinit var eqView: EqView
    private var recorder: AudioRecord? = null
    private var worker: Thread? = null
    @Volatile private var running = false

    external fun nativeGain(sample: Float, gainDb: Float): Float
    companion object { init { System.loadLibrary("stylo_android") } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eqView = EqView()
        setContentView(eqView)
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10) else startCapture()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (requestCode == 10 && results.isNotEmpty() && results[0] == PackageManager.PERMISSION_GRANTED) startCapture()
    }

    private fun startCapture() {
        val sampleRate = 44100
        val min = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        if (min <= 0) return
        recorder = AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, min * 2)
        recorder?.startRecording(); running = true
        worker = Thread {
            val n = 256; val input = ShortArray(n); val spectrum = FloatArray(128)
            while (running) {
                val read = recorder?.read(input, 0, input.size) ?: 0
                if (read > 0) {
                    for (k in spectrum.indices) {
                        var re=0.0; var im=0.0
                        for (i in 0 until read) { val w=0.5*(1.0-cos(2.0*PI*i/(read-1).coerceAtLeast(1))); val x=input[i]/32768.0*w; val p=2.0*PI*k*i/read; re+=x*cos(p); im-=x*sin(p) }
                        spectrum[k]=(20.0*log10(sqrt(re*re+im*im)+1e-6)).toFloat().coerceIn(-90f,12f)
                    }
                    runOnUiThread { eqView.setSpectrum(spectrum) }
                }
            }
        }.also { it.start() }
    }

    override fun onDestroy() { running=false; try { recorder?.stop() } catch(_:Exception){}; recorder?.release(); recorder=null; worker?.interrupt(); worker=null; super.onDestroy() }

    inner class EqView : View(this) {
        private val paint=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        private var spectrum=FloatArray(128){-80f}; private var freq=1000f; private var gain=0f; private var dragging=false
        init { paint.typeface=android.graphics.Typeface.create("sans",android.graphics.Typeface.NORMAL) }
        fun setSpectrum(v:FloatArray){ spectrum=v; invalidate() }
        override fun onDraw(c:android.graphics.Canvas){
            c.drawColor(android.graphics.Color.rgb(9,11,14)); val w=width.toFloat(); val h=height.toFloat(); val top=90f; val bottom=h-150f; val left=28f; val right=w-18f
            paint.style=android.graphics.Paint.Style.STROKE; paint.strokeWidth=1f; paint.color=android.graphics.Color.rgb(45,48,55)
            for(i in 0..6){val y=top+(bottom-top)*i/6f;c.drawLine(left,y,right,y,paint)}
            val fs=floatArrayOf(20f,100f,1000f,10000f,20000f); for(f in fs){val x=xForFreq(f,left,right);c.drawLine(x,top,x,bottom,paint)}
            paint.color=android.graphics.Color.rgb(55,170,220);paint.strokeWidth=2f;var lx=left;var ly=bottom
            for(i in spectrum.indices){val x=left+(right-left)*i/(spectrum.size-1);val y=bottom-((spectrum[i]+90f)/102f)*(bottom-top);if(i>0)c.drawLine(lx,ly,x,y,paint);lx=x;ly=y}
            paint.color=android.graphics.Color.WHITE;paint.strokeWidth=4f;var px=left;var py=eqY(left,left,right,top,bottom)
            for(i in 1..160){val x=left+(right-left)*i/160f;val y=eqY(x,left,right,top,bottom);c.drawLine(px,py,x,y,paint);px=x;py=y}
            val nx=xForFreq(freq,left,right);val ny=eqY(nx,left,right,top,bottom);paint.style=android.graphics.Paint.Style.FILL;paint.color=android.graphics.Color.rgb(40,220,190);c.drawCircle(nx,ny,11f,paint)
            paint.color=android.graphics.Color.WHITE;paint.textSize=34f;c.drawText("STYLO EQ",28f,52f,paint);paint.textSize=16f;paint.color=android.graphics.Color.LTGRAY;c.drawText("Realtime Spectrum · Interactive EQ",28f,76f,paint);paint.textSize=14f;paint.color=android.graphics.Color.GRAY
            for(f in fs)c.drawText(if(f>=1000)"${(f/1000).toInt()}k" else "${f.toInt()}",xForFreq(f,left,right)-12,bottom+24,paint)
            paint.color=android.graphics.Color.WHITE;paint.textSize=18f;c.drawText("${freq.toInt()} Hz    ${"%.1f".format(gain)} dB    Q 1.00",28f,h-105f,paint);paint.color=android.graphics.Color.LTGRAY;paint.textSize=14f;c.drawText("Drag node: frequency + gain",28f,h-75f,paint);c.drawText("Audio input",28f,h-40f,paint)
        }
        private fun xForFreq(f:Float,l:Float,r:Float)=l+(log10(f/20f)/log10(1000f))*(r-l)
        private fun freqForX(x:Float,l:Float,r:Float)=20f*Math.pow(1000.0,((x-l)/(r-l)).toDouble()).toFloat()
        private fun eqY(x:Float,l:Float,r:Float,t:Float,b:Float):Float{val center=(t+b)/2f;val dx=(x-xForFreq(freq,l,r))/(r-l);val shape=kotlin.math.exp(-dx*dx*90f);return center-gain*shape*(b-t)/204f}
        override fun onTouchEvent(e:MotionEvent):Boolean{val l=28f;val r=width-18f;val t=90f;val b=height-150f;when(e.actionMasked){MotionEvent.ACTION_DOWN->{val nx=xForFreq(freq,l,r);val ny=eqY(nx,l,r,t,b);dragging=abs(e.x-nx)<70f&&abs(e.y-ny)<70f;return true};MotionEvent.ACTION_MOVE->if(dragging){freq=freqForX(e.x.coerceIn(l,r),l,r).coerceIn(20f,20000f);gain=((b/2f-e.y)/(b-t)*102f).coerceIn(-18f,18f);invalidate();return true};MotionEvent.ACTION_UP->{dragging=false;return true}};return true}
    }
}
