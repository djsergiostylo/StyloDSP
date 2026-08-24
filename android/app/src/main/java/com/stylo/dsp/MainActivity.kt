package com.stylo.dsp

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Process
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.PI

class MainActivity : ComponentActivity() {
    private lateinit var view: EqView
    private var recorder: AudioRecord? = null
    private var thread: Thread? = null
    private var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = EqView()
        setContentView(view)
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
        else startAudio()
    }

    override fun onRequestPermissionsResult(r: Int, p: Array<out String>, g: IntArray) {
        super.onRequestPermissionsResult(r, p, g)
        if (r == 10 && g.firstOrNull() == PackageManager.PERMISSION_GRANTED) startAudio()
        else Toast.makeText(this, "Se necesita el micrófono para el analizador", Toast.LENGTH_LONG).show()
    }

    private fun startAudio() {
        if (running) return
        val rate = 44100
        val n = 2048
        val min = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        recorder = AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, max(min, n * 2))
        recorder?.startRecording(); running = true
        thread = Thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
            val samples = ShortArray(n)
            val mags = FloatArray(n / 2)
            while (running) {
                val got = recorder?.read(samples, 0, samples.size) ?: 0
                if (got > 0) {
                    for (k in 0 until mags.size) {
                        var re = 0.0; var im = 0.0
                        for (i in 0 until got) {
                            val w = 0.5 - 0.5 * cos(2.0 * PI * i / max(1, got - 1))
                            val a = 2.0 * PI * k * i / got
                            val x = samples[i] / 32768.0 * w
                            re += x * cos(a); im -= x * kotlin.math.sin(a)
                        }
                        mags[k] = (20.0 * log10(max(1e-6, kotlin.math.sqrt(re*re + im*im) / got))).toFloat()
                    }
                    runOnUiThread { view.setSpectrum(mags) }
                }
            }
        }.also { it.start() }
    }

    override fun onDestroy() {
        running = false
        thread?.join(250)
        recorder?.stop(); recorder?.release(); recorder = null
        super.onDestroy()
    }

    private inner class EqView : View(this) {
        private val paint = android.graphics.Paint(3)
        private var spectrum = FloatArray(1024)
        private var freq = 1000.0
        private var gain = 0.0
        private var dragging = false

        fun setSpectrum(v: FloatArray) { spectrum = v; invalidate() }
        private fun xToFreq(x: Float, w: Float): Double = 20.0 * exp((ln(20000.0/20.0) * x / w))
        private fun freqToX(f: Double, w: Float): Float = (ln(f/20.0) / ln(20000.0/20.0) * w).toFloat()
        private fun yToGain(y: Float, h: Float): Double = 12.0 - 24.0 * y / h
        private fun gainToY(g: Double, h: Float): Float = ((12.0-g)/24.0*h).toFloat()

        override fun onDraw(c: android.graphics.Canvas) {
            super.onDraw(c); val w = width.toFloat(); val h = height.toFloat()
            c.drawColor(android.graphics.Color.rgb(9,10,12))
            paint.style = android.graphics.Paint.Style.STROKE; paint.strokeWidth = 1f; paint.color = 0x30363C42
            for (i in 0..10) { val y=h*0.12f+i*h*0.76f; c.drawLine(0f,y,w,y,paint) }
            val fs = floatArrayOf(20f,100f,1000f,10000f,20000f)
            for (f in fs) { val x=freqToX(f.toDouble(),w); c.drawLine(x,h*0.08f,x,h*0.88f,paint) }
            paint.color=0xAA67DDF2.toInt(); paint.strokeWidth=2f
            val path=android.graphics.Path(); var started=false
            for (i in 1 until spectrum.size) {
                val f=i.toDouble()/spectrum.size*22050.0; if (f<20 || f>20000) continue
                val x=freqToX(f,w); val db=min(0f,max(-80f,spectrum[i])); val y=h*0.88f-(db+80f)/80f*h*0.76f
                if(!started){path.moveTo(x,y);started=true}else path.lineTo(x,y)
            }; c.drawPath(path,paint)
            paint.color=0xFFE9F4F7.toInt(); paint.strokeWidth=4f
            val nx=freqToX(freq,w); val ny=gainToY(gain,h); val curve=android.graphics.Path(); curve.moveTo(0f,h*0.5f)
            for(xi in 0..w.toInt() step 8){ val f=xToFreq(xi.toFloat(),w); val d=exp(-0.5*((ln(f/freq)/ln(2.0))*3.0).let{it*it}); val y=gainToY(gain*d,h); if(xi==0)curve.moveTo(0f,y) else curve.lineTo(xi.toFloat(),y)}; c.drawPath(curve,paint)
            paint.style=android.graphics.Paint.Style.FILL; paint.color=0xFF7DE7FF.toInt(); c.drawCircle(nx,ny,13f,paint)
            paint.color=0xFFB8C3C8.toInt(); paint.textSize=28f; c.drawText("STYLO EQ",24f,42f,paint)
            paint.textSize=18f; c.drawText("${"%.0f".format(freq)} Hz   ${"%+.1f".format(gain)} dB",24f,h-58f,paint)
            c.drawText("20 Hz",10f,h-22f,paint); c.drawText("1 kHz",w/2-25f,h-22f,paint); c.drawText("20 kHz",w-75f,h-22f,paint)
        }
        override fun onTouchEvent(e: MotionEvent): Boolean {
            val w=width.toFloat(); val h=height.toFloat(); val nx=freqToX(freq,w); val ny=gainToY(gain,h)
            when(e.action){MotionEvent.ACTION_DOWN -> {dragging=abs(e.x-nx)<40 && abs(e.y-ny)<40; return true}; MotionEvent.ACTION_MOVE -> if(dragging){freq=min(20000.0,max(20.0,xToFreq(e.x,w))); gain=min(12.0,max(-12.0,yToGain(e.y,h))); invalidate(); return true}; MotionEvent.ACTION_UP -> {dragging=false; return true}}
            return true
        }
    }
}
