package com.stylo.dsp

import android.media.audiofx.DynamicsProcessing
import android.os.Build

class NativeEqBridge(private val audioSessionId: Int, private val bandCount: Int = 31) {
    private var fx: DynamicsProcessing? = null
    var available: Boolean = false
        private set

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && audioSessionId != 0) {
            runCatching {
                val cfg = DynamicsProcessing.Config.Builder(1, false, 0, false, 0, true, bandCount, false).build()
                fx = DynamicsProcessing(0, audioSessionId).apply { setProperties(cfg); enabled = true }
                available = true
            }.onFailure { available = false }
        }
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
