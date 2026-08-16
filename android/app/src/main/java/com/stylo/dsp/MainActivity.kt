package com.stylo.dsp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        status = TextView(this).apply {
            text = "STYLO DSP Core\nEstado: detenido"
            textSize = 20f
            setPadding(48, 48, 48, 48)
        }

        val start = Button(this).apply {
            text = "Iniciar audio"
            setOnClickListener {
                val ok = NativeAudio.start()
                status.text = if (ok) {
                    "STYLO DSP Core\nEstado: AUDIO ACTIVO\nRust Gain + Oboe/AAudio"
                } else {
                    "STYLO DSP Core\nEstado: ERROR AL ABRIR AUDIO"
                }
            }
        }

        val stop = Button(this).apply {
            text = "Detener audio"
            setOnClickListener {
                NativeAudio.stop()
                status.text = "STYLO DSP Core\nEstado: detenido"
            }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(status)
            addView(start)
            addView(stop)
        })
    }

    override fun onDestroy() {
        NativeAudio.stop()
        super.onDestroy()
    }
}

private object NativeAudio {
    init {
        System.loadLibrary("stylo_dsp_bridge")
    }

    external fun start(): Boolean
    external fun stop()
}
