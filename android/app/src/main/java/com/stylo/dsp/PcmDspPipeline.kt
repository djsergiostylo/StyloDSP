package com.stylo.dsp

import kotlin.math.max

/**
 * Owns the realtime PCM processing path. Control changes are published by swapping
 * an already-configured EqBank reference, keeping the audio callback free of locks
 * and allocations.
 */
class PcmDspPipeline(sampleRate: Double, bandCount: Int = 31) {
    private val sampleRate = sampleRate
    @Volatile private var active: EqBank = EqBank(sampleRate, MutableList(bandCount) { EqBand(1000.0, 0.0) })
    @Volatile private var bypass = false

    init { active.configureAll() }

    fun configure(bands: List<EqBand>) {
        val next = EqBank(sampleRate, bands.map { it.copy() }.toMutableList())
        next.configureAll()
        active = next
    }

    fun setBypass(value: Boolean) { bypass = value }

    /** Interleaved stereo float PCM, in-place. No allocation on the processing path. */
    fun process(buffer: FloatArray, frames: Int) {
        if (bypass) return
        val bank = active
        val n = max(0, minOf(frames, buffer.size / 2))
        for (i in 0 until n) {
            val p = i * 2
            buffer[p] = bank.process(buffer[p]).coerceIn(-1f, 1f).toFloat()
            buffer[p + 1] = bank.process(buffer[p + 1]).coerceIn(-1f, 1f).toFloat()
        }
    }
}
