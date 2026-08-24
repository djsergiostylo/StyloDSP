package com.stylo.dsp

/** Realtime stereo PCM processing. */
class PcmDspPipeline(sampleRate: Double, bandCount: Int = 31) {
    private val rate = sampleRate
    @Volatile private var active = StereoBank(rate, MutableList(bandCount) { EqBand(1000.0, 0.0) })
    @Volatile private var bypass = false

    init { active.configureAll() }

    fun configure(bands: List<EqBand>) {
        val next = StereoBank(rate, bands.map { it.copy() }.toMutableList())
        next.configureAll()
        active = next
    }

    fun setBypass(value: Boolean) { bypass = value }

    fun process(buffer: FloatArray, frames: Int) {
        if (bypass) return
        val bank = active
        val n = minOf(frames.coerceAtLeast(0), buffer.size / 2)
        for (i in 0 until n) {
            val p = i * 2
            buffer[p] = bank.left.process(buffer[p].toDouble()).coerceIn(-1.0, 1.0).toFloat()
            buffer[p + 1] = bank.right.process(buffer[p + 1].toDouble()).coerceIn(-1.0, 1.0).toFloat()
        }
    }

    private class StereoBank(sampleRate: Double, bands: List<EqBand>) {
        val left = EqBank(sampleRate, bands.map { it.copy() })
        val right = EqBank(sampleRate, bands.map { it.copy() })
        fun configureAll() { left.configureAll(); right.configureAll() }
    }
}
