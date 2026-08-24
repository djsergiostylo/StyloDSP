package com.stylo.dsp

import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.Equalizer

class AudioEqProcessor(private val model: EqModel) {
    private var dynamics: DynamicsProcessing? = null
    private var legacy: Equalizer? = null

    fun attach(audioSession: Int) {
        release()
        try {
            if (android.os.Build.VERSION.SDK_INT >= 28) {
                val cfg = DynamicsProcessing.Config.Builder(
                    DynamicsProcessing.VARIANT_FAVOR_TIME_RESOLUTION,
                    2, false, 0, false, 0, true, 31, true
                ).build()
                dynamics = DynamicsProcessing(0, audioSession, cfg).also { it.enabled = true }
                apply()
            } else {
                legacy = Equalizer(0, audioSession).also { it.enabled = true }
                apply()
            }
        } catch (_: Throwable) { release() }
    }

    fun apply() {
        val dp = dynamics
        if (dp != null && android.os.Build.VERSION.SDK_INT >= 28) {
            val eq = dp.getPostEqByChannelIndex(0)
            for (i in 0 until minOf(31, eq.bandCount)) {
                val b = model.graphic[i]
                eq.setBand(i, DynamicsProcessing.EqBand(!model.bypass && b.enabled, b.freq.toFloat(), if (model.bypass || model.ab) 0f else b.gain))
            }
            dp.setPostEqByChannelIndex(1, eq)
            return
        }
        val legacyEq = legacy ?: return
        val count = legacyEq.numberOfBands.toInt()
        val lo = legacyEq.bandLevelRange[0].toInt() / 100f
        val hi = legacyEq.bandLevelRange[1].toInt() / 100f
        for (i in 0 until count) {
            val f = legacyEq.getCenterFreq(i.toShort()) / 1000.0
            val band = model.active.minByOrNull { kotlin.math.abs(kotlin.math.ln(kotlin.math.max(20.0, f) / it.freq)) }
            val gain = if (model.bypass || model.ab) 0f else (band?.gain ?: 0f)
            try { legacyEq.setBandLevel(i.toShort(), (gain.coerceIn(lo, hi) * 100).toInt().toShort()) } catch (_: Throwable) {}
        }
    }

    fun release() { dynamics?.release(); dynamics = null; legacy?.release(); legacy = null }
}
