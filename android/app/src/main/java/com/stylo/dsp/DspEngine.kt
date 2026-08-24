package com.stylo.dsp

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt

/** Lightweight realtime DSP primitives. No allocations are made in FFT.process(). */
class Fft(private val size: Int) {
    private val real = DoubleArray(size)
    private val imag = DoubleArray(size)
    private val window = DoubleArray(size) { i -> 0.5 - 0.5 * cos(2.0 * PI * i / (size - 1)) }
    private val bitReverse = IntArray(size)

    init {
        require(size > 1 && size and (size - 1) == 0)
        var j = 0
        for (i in 1 until size) {
            var bit = size shr 1
            while (j and bit != 0) { j = j xor bit; bit = bit shr 1 }
            j = j xor bit
            bitReverse[i] = j
        }
    }

    fun process(input: ShortArray, count: Int, magnitudesDb: FloatArray) {
        for (i in 0 until size) {
            val x = if (i < count) input[i].toDouble() / 32768.0 else 0.0
            real[bitReverse[i]] = x * window[i]
            imag[bitReverse[i]] = 0.0
        }
        var len = 2
        while (len <= size) {
            val half = len shr 1
            val theta = -2.0 * PI / len
            var base = 0
            while (base < size) {
                for (j in 0 until half) {
                    val a = theta * j
                    val wr = cos(a); val wi = sin(a)
                    val i0 = base + j; val i1 = i0 + half
                    val tr = wr * real[i1] - wi * imag[i1]
                    val ti = wr * imag[i1] + wi * real[i1]
                    real[i1] = real[i0] - tr; imag[i1] = imag[i0] - ti
                    real[i0] += tr; imag[i0] += ti
                }
                base += len
            }
            len = len shl 1
        }
        val n = minOf(magnitudesDb.size, size / 2)
        for (i in 0 until n) {
            val mag = sqrt(real[i] * real[i] + imag[i] * imag[i]) / size
            magnitudesDb[i] = (20.0 * kotlin.math.log10(maxOf(1e-7, mag))).toFloat()
        }
    }
}

enum class FilterType { PEAK, LOW_SHELF, HIGH_SHELF, LOW_PASS, HIGH_PASS, NOTCH, BAND_PASS, ALL_PASS, TILT }

data class EqBand(var frequency: Double, var gainDb: Double, var q: Double = 1.0, var type: FilterType = FilterType.PEAK, var enabled: Boolean = true)

/** Stereo/mono-safe biquad coefficients. Process in-place for low allocation. */
class Biquad(private val sampleRate: Double) {
    private var b0 = 1.0; private var b1 = 0.0; private var b2 = 0.0
    private var a1 = 0.0; private var a2 = 0.0
    private var z1 = 0.0; private var z2 = 0.0

    fun configure(band: EqBand) {
        if (!band.enabled) { b0 = 1.0; b1 = 0.0; b2 = 0.0; a1 = 0.0; a2 = 0.0; return }
        val f = band.frequency.coerceIn(10.0, sampleRate * 0.49)
        val q = band.q.coerceIn(0.1, 18.0)
        val A = 10.0.pow(band.gainDb / 40.0)
        val w0 = 2.0 * PI * f / sampleRate
        val c = cos(w0); val s = sin(w0); val alpha = s / (2.0 * q)
        var B0: Double; var B1: Double; var B2: Double; var A0: Double; var A1: Double; var A2: Double
        when (band.type) {
            FilterType.PEAK -> { B0=1+alpha*A; B1=-2*c; B2=1-alpha*A; A0=1+alpha/A; A1=-2*c; A2=1-alpha/A }
            FilterType.LOW_SHELF -> { val beta=2*sqrt(A)*alpha; B0=A*((A+1)-(A-1)*c+beta); B1=2*A*((A-1)-(A+1)*c); B2=A*((A+1)-(A-1)*c-beta); A0=(A+1)+(A-1)*c+beta; A1=-2*((A-1)+(A+1)*c); A2=(A+1)+(A-1)*c-beta }
            FilterType.HIGH_SHELF -> { val beta=2*sqrt(A)*alpha; B0=A*((A+1)+(A-1)*c+beta); B1=-2*A*((A-1)+(A+1)*c); B2=A*((A+1)+(A-1)*c-beta); A0=(A+1)-(A-1)*c+beta; A1=2*((A-1)-(A+1)*c); A2=(A+1)-(A-1)*c-beta }
            FilterType.LOW_PASS -> { B0=(1-c)/2; B1=1-c; B2=(1-c)/2; A0=1+alpha; A1=-2*c; A2=1-alpha }
            FilterType.HIGH_PASS -> { B0=(1+c)/2; B1=-(1+c); B2=(1+c)/2; A0=1+alpha; A1=-2*c; A2=1-alpha }
            FilterType.NOTCH -> { B0=1; B1=-2*c; B2=1; A0=1+alpha; A1=-2*c; A2=1-alpha }
            FilterType.BAND_PASS -> { B0=alpha; B1=0; B2=-alpha; A0=1+alpha; A1=-2*c; A2=1-alpha }
            FilterType.ALL_PASS -> { B0=1-alpha; B1=-2*c; B2=1+alpha; A0=1+alpha; A1=-2*c; A2=1-alpha }
            FilterType.TILT -> { B0=1+band.gainDb/24.0; B1=-2*c; B2=1-band.gainDb/24.0; A0=1+alpha; A1=-2*c; A2=1-alpha }
        }
        b0=B0/A0; b1=B1/A0; b2=B2/A0; a1=A1/A0; a2=A2/A0
    }

    fun process(x: Double): Double {
        val y = b0*x + z1
        z1 = b1*x - a1*y + z2
        z2 = b2*x - a2*y
        return y
    }

    fun reset() { z1=0.0; z2=0.0 }
    private fun Double.pow(p: Double): Double = kotlin.math.exp(kotlin.math.ln(this) * p)
}

class EqBank(private val sampleRate: Double, val bands: MutableList<EqBand>) {
    private val filters = bands.map { Biquad(sampleRate) }
    fun configureAll() { bands.forEachIndexed { i,b -> filters[i].configure(b) } }
    fun process(x: Double): Double { var y=x; for (f in filters) y=f.process(y); return y }
    fun reset() = filters.forEach { it.reset() }
}
