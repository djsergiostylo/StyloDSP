package com.stylo.dsp

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

class EqModel {
    data class Band(var freq: Double, var gain: Float = 0f, var q: Float = 1f, var enabled: Boolean = true)
    val graphic = MutableList(31) { i -> Band(20.0 * (1000.0).pow(i / 30.0)) }
    val parametric = MutableList(8) { i -> Band(doubleArrayOf(60.0,120.0,250.0,500.0,1000.0,2500.0,8000.0,14000.0)[i]) }
    var selected = 15
    var parametricMode = false
    var bypass = false
    var ab = false
    val active: MutableList<Band> get() = if (parametricMode) parametric else graphic
    fun nearestBand(freq: Double): Int {
        var best = 0; var d = Double.MAX_VALUE
        active.forEachIndexed { i, b -> val x = kotlin.math.abs(ln(freq / b.freq)); if (x < d) { d = x; best = i } }
        return best
    }
    fun responseDb(freq: Double): Double {
        if (bypass || ab) return 0.0
        var sum = 0.0
        for (b in active) if (b.enabled && b.gain != 0f) {
            val z = ln(freq / b.freq) / ln(2.0)
            val width = if (parametricMode) 1.0 / b.q.coerceAtLeast(.25f) else .42
            sum += b.gain * exp(-0.5 * (z / width) * (z / width))
        }
        return sum.coerceIn(-24.0, 24.0)
    }
}
