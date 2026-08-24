package com.stylo.dsp

/**
 * Realtime stereo PCM processing. Left and right channels have independent filter state.
 * Control changes build the next banks off the audio path and publish them atomically.
 */
class PcmDspPipeline(sampleRate: Double, bandCount: Int = 31) {
    private val sampleRate = sampleRate
    @Volatile private var active: StereoBank = StereoBank(sampleRate, MutableList(bandCount) { EqBand(1000.0, 0.0) })
    @Volatile private var bypass = false

    init { active.configureAll() }

    fun configure(bands: List<EqBand>) {
        val next = StereoBank(sampleRate, bands.map { it.copy() }.toMutableList())
        next.configureAll()
        active = next
    }

    fun setBypass(value: Boolean) { bypass = value }

    /** Interleaved stereo float PCM in [-1,1]. No heap allocation occurs here. */
    fun process(buffer: FloatArray, frames: Int) {
        if (bypass) return
        val bank = active
        val n = minOf(frames.coerceAtLeast(0), buffer.size / 2)
        for (i in 0 until n) {
            val p = i * 2
            buffer[p] = bank.left.process(buffer[p]).coerceIn(-1f, 1f)
            buffer[p + 1] = bank.right.process(buffer[p + 1]).coerceIn(-1f, 1f)
        }
    }

    private class StereoBank(sampleRate: Double, bands: MutableList<EqBand>) {
        val left = EqBank(sampleRate, bands.map { it.copy() }.toMutableList())
        val right = EqBank(sampleRate, bands.map { it.copy() }.toMutableList())
        fun configureAll() { left.configureAll(); right.configureAll() }
    }
}
