package com.stylo.dsp

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt

/** Realtime DSP primitives. process() performs no heap allocation. */
class Fft(private val size: Int) {
    private val real = DoubleArray(size)
    private val imag = DoubleArray(size)
    private val window = DoubleArray(size) { i -> 0.5 - 0.5 * cos(2.0 * PI * i / (size - 1)) }
    private val bitReverse = IntArray(size)
    private val twiddleCos = DoubleArray(size / 2)
    private val twiddleSin = DoubleArray(size / 2)

    init {
        require(size > 1 && size and (size - 1) == 0) { "FFT size must be a power of two" }
        var j = 0
        for (i in 1 until size) {
            var bit = size shr 1
            while (j and bit != 0) { j = j xor bit; bit = bit shr 1 }
            j = j xor bit
            bitReverse[i] = j
        }
        for (i in twiddleCos.indices) {
            val a = -2.0 * PI * i / size
            twiddleCos[i] = cos(a)
            twiddleSin[i] = sin(a)
        }
    }

    fun process(input: ShortArray, count: Int, magnitudesDb: FloatArray) {
        val safeCount = count.coerceIn(0, size)
        for (i in 0 until size) {
            val x = if (i < safeCount) input[i].toDouble() / 32768.0 else 0.0
            real[bitReverse[i]] = x * window[i]
            imag[bitReverse[i]] = 0.0
        }
        var len = 2
        while (len <= size) {
            val half = len shr 1
            val step = size / len
            var base = 0
            while (base < size) {
                var j = 0
                var tw = 0
                while (j < half) {
                    val wr = twiddleCos[tw]
                    val wi = twiddleSin[tw]
                    val i0 = base + j
                    val i1 = i0 + half
                    val tr = wr * real[i1] - wi * imag[i1]
                    val ti = wr * imag[i1] + wi * real[i1]
                    val r0 = real[i0]
                    val im0 = imag[i0]
                    real[i1] = r0 - tr
                    imag[i1] = im0 - ti
                    real[i0] = r0 + tr
                    imag[i0] = im0 + ti
                    j++
                    tw += step
                }
                base += len
            }
            len = len shl 1
        }
        val n = minOf(magnitudesDb.size, size / 2)
        for (i in 0 until n) {
            val mag = sqrt(real[i] * real[i] + imag[i] * imag[i]) / size
            magnitudesDb[i] = (20.0 * log10(maxOf(1e-7, mag))).toFloat()
        }
    }
}

enum class FilterType { PEAK, LOW_SHELF, HIGH_SHELF, LOW_PASS, HIGH_PASS, NOTCH, BAND_PASS, ALL_PASS, TILT }

data class EqBand(
    var frequency: Double,
    var gainDb: Double,
    var q: Double = 1.0,
    var type: FilterType = FilterType.PEAK,
    var enabled: Boolean = true
)

/** One biquad with state kept between PCM blocks. */
class Biquad(private val sampleRate: Double) {
    private var b0 = 1.0; private var b1 = 0.0; private var b2 = 0.0
    private var a1 = 0.0; private var a2 = 0.0
    private var z1 = 0.0; private var z2 = 0.0

    fun configure(band: EqBand) {
        if (!band.enabled) { b0=1.0; b1=0.0; b2=0.0; a1=0.0; a2=0.0; return }
        val f = band.frequency.coerceIn(10.0, sampleRate * 0.49)
        val q = band.q.coerceIn(0.1, 18.0)
        val A = exp(ln(10.0) * band.gainDb / 40.0)
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
            FilterType.TILT -> { val slope=band.gainDb.coerceIn(-24.0,24.0)/24.0; B0=1+slope; B1=-2*c; B2=1-slope; A0=1+alpha; A1=-2*c; A2=1-alpha }
        }
        b0=B0/A0; b1=B1/A0; b2=B2/A0; a1=A1/A0; a2=A2/A0
    }

    fun process(x: Double): Double {
        val y=b0*x+z1
        z1=b1*x-a1*y+z2
        z2=b2*x-a2*y
        return y
    }

    fun reset(){z1=0.0;z2=0.0}
}

/** Dynamic filter bank. Control changes rebuild/configure outside process(). */
class EqBank(private val sampleRate: Double, initialBands: List<EqBand> = emptyList()) {
    private var bands = initialBands.map { it.copy() }
    private var filters: Array<Biquad> = Array(bands.size) { Biquad(sampleRate) }

    fun setBands(newBands: List<EqBand>) {
        bands = newBands.map { it.copy() }
        filters = Array(bands.size) { Biquad(sampleRate) }
        configureAll()
    }

    fun configureAll() { bands.forEachIndexed { i, b -> filters[i].configure(b) } }
    fun process(x: Double): Double { var y=x; for (f in filters) y=f.process(y); return y }
    fun reset(){ filters.forEach { it.reset() } }
}

/** Simple safety stage. It never allocates and only limits overs. */
class SafetyLimiter(private val drive: Double = 1.0) {
    fun process(x: Double): Double {
        val y = x * drive
        return when {
            y > 1.0 -> 1.0
            y < -1.0 -> -1.0
            else -> y
        }
    }
}
