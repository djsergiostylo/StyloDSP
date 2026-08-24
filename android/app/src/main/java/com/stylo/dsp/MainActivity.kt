package com.stylo.dsp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Process
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : Activity() {
    private lateinit var view: EqView
    private var recorder: AudioRecord? = null
    private var thread: Thread? = null
    @Volatile private var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = EqView()
        setContentView(view)
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_AUDIO)
        } else {
            startAudio()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_AUDIO && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            startAudio()
        } else {
            Toast.makeText(this, "Se necesita el micrófono para el analizador", Toast.LENGTH_LONG).show()
        }
    }

    private fun startAudio() {
        if (running) return
        val rate = 44100
        val n = 2048
        val minBuffer = AudioRecord.getMinBufferSize(
            rate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (minBuffer <= 0) {
            Toast.makeText(this, "No se pudo inicializar el audio", Toast.LENGTH_LONG).show()
            return
        }
        recorder = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            rate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            max(minBuffer, n * 2)
        )
        try {
            recorder?.startRecording()
        } catch (_: Throwable) {
            recorder?.release()
            recorder = null
            Toast.makeText(this, "No se pudo iniciar el micrófono", Toast.LENGTH_LONG).show()
            return
        }
        running = true
        thread = Thread {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
            val samples = ShortArray(n)
            val mags = FloatArray(n / 2)
            while (running) {
                val got = recorder?.read(samples, 0, samples.size) ?: 0
                if (got > 0) {
                    val limit = min(got, samples.size)
                    for (k in mags.indices) {
                        var re = 0.0
                        var im = 0.0
                        for (i in 0 until limit) {
                            val w = 0.5 - 0.5 * cos(2.0 * PI * i / max(1, limit - 1))
                            val a = 2.0 * PI * k * i / limit
                            val x = samples[i] / 32768.0 * w
                            re += x * cos(a)
                            im -= x * sin(a)
                        }
                        mags[k] = (20.0 * log10(max(1e-6, sqrt(re * re + im * im) / limit))).toFloat()
                    }
                    val frame = mags.copyOf()
                    runOnUiThread { if (running) view.setSpectrum(frame) }
                }
            }
        }.also { it.start() }
    }

    override fun onDestroy() {
        running = false
        thread?.join(250)
        thread = null
        try { recorder?.stop() } catch (_: Throwable) { }
        recorder?.release()
        recorder = null
        super.onDestroy()
    }

    private inner class EqView : View(this) {
        private val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        private var spectrum = FloatArray(1024)
        private var freq = 1000.0
        private var gain = 0.0
        private var dragging = false

        fun setSpectrum(v: FloatArray) { spectrum = v; invalidate() }
        private fun xToFreq(x: Float, w: Float): Double = 20.0 * exp(ln(1000.0) * x / w)
        private fun freqToX(f: Double, w: Float): Float = (ln(f / 20.0) / ln(1000.0) * w).toFloat()
        private fun yToGain(y: Float, h: Float): Double = 12.0 - 24.0 * y / h
        private fun gainToY(g: Double, h: Float): Float = ((12.0 - g) / 24.0 * h).toFloat()

        override fun onDraw(c: android.graphics.Canvas) {
            super.onDraw(c)
            val w = width.toFloat()
            val h = height.toFloat()
            c.drawColor(android.graphics.Color.rgb(9, 10, 12))
            paint.style = android.graphics.Paint.Style.STROKE
            paint.strokeWidth = 1f
            paint.color = 0x30363C42
            for (i in 0..10) {
                val y = h * 0.12f + i * h * 0.76f / 10f
                c.drawLine(0f, y, w, y, paint)
            }
            val fs = floatArrayOf(20f, 100f, 1000f, 10000f, 20000f)
            for (f in fs) {
                val x = freqToX(f.toDouble(), w)
                c.drawLine(x, h * 0.08f, x, h * 0.88f, paint)
            }
            paint.color = 0xAA67DDF2.toInt()
            paint.strokeWidth = 2f
            val path = android.graphics.Path()
            var started = false
            for (i in 1 until spectrum.size) {
                val f = i.toDouble() / spectrum.size * 22050.0
                if (f < 20 || f > 20000) continue
                val x = freqToX(f, w)
                val db = min(0f, max(-80f, spectrum[i]))
                val y = h * 0.88f - (db + 80f) / 80f * h * 0.76f
                if (!started) { path.moveTo(x, y); started = true } else path.lineTo(x, y)
            }
            c.drawPath(path, paint)
            paint.color = 0xFFE9F4F7.toInt()
            paint.strokeWidth = 4f
            val nx = freqToX(freq, w)
            val ny = gainToY(gain, h)
            val curve = android.graphics.Path()
            for (xi in 0..w.toInt() step 8) {
                val f = xToFreq(xi.toFloat(), w)
                val z = ln(f / freq) / ln(2.0)
                val d = exp(-0.5 * (z * 3.0) * (z * 3.0))
                val y = gainToY(gain * d, h)
                if (xi == 0) curve.moveTo(0f, y) else curve.lineTo(xi.toFloat(), y)
            }
            c.drawPath(curve, paint)
            paint.style = android.graphics.Paint.Style.FILL
            paint.color = 0xFF7DE7FF.toInt()
            c.drawCircle(nx, ny, 13f, paint)
            paint.color = 0xFFB8C3C8.toInt()
            paint.textSize = 28f
            c.drawText("STYLO EQ", 24f, 42f, paint)
            paint.textSize = 18f
            c.drawText("${"%.0f".format(freq)} Hz   ${"%+.1f".format(gain)} dB", 24f, h - 58f, paint)
            c.drawText("20 Hz", 10f, h - 22f, paint)
            c.drawText("1 kHz", w / 2f - 25f, h - 22f, paint)
            c.drawText("20 kHz", w - 75f, h - 22f, paint)
        }

        override fun onTouchEvent(e: MotionEvent): Boolean {
            val w = width.toFloat()
            val h = height.toFloat()
            val nx = freqToX(freq, w)
            val ny = gainToY(gain, h)
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dragging = abs(e.x - nx) < 40 && abs(e.y - ny) < 40
                    return true
                }
                MotionEvent.ACTION_MOVE -> if (dragging) {
                    freq = min(20000.0, max(20.0, xToFreq(e.x, w)))
                    gain = min(12.0, max(-12.0, yToGain(e.y, h)))
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    dragging = false
                    return true
                }
            }
            return true
        }
    }

    companion object { private const val REQUEST_AUDIO = 10 }
}
