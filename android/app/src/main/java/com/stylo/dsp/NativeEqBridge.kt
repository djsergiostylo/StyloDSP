package com.stylo.dsp

import android.media.audiofx.DynamicsProcessing

class NativeEqBridge(private val audioSessionId: Int, private val bandCount: Int = 31) {
    private var fx: DynamicsProcessing? = null
    var available: Boolean = false
        private set

    init {
        runCatching {
            val cfg = DynamicsProcessing.Config.Builder(44100, true, 0, 0, 0, 0, bandCount, 0)
                .setPreferredFrameDuration(10f)
                .build()
            fx = DynamicsProcessing(0, audioSessionId).apply {
                enabled = true
                setProperties(cfg)
            }
            available = true
        }.onFailure { available = false }
    }

    fun setGraphicGain(index: Int, gainDb: Float) {
        val p = fx ?: return
        if (index !in 0 until bandCount) return
        runCatching {
            val band = p.getPostEqBand(0, index)
            band.isEnabled = true
            band.gain = gainDb.coerceIn(-24f, 24f)
            p.setPostEqBand(0, index, band)
        }
    }

    fun setBypass(bypass: Boolean) { runCatching { fx?.enabled = !bypass } }
    fun release() { runCatching { fx?.release() }; fx = null }
}
