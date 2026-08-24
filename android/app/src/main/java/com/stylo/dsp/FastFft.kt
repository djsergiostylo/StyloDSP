package com.stylo.dsp

/** Allocation-free radix-2 FFT for realtime spectrum analysis. */
class FastFft(private val n: Int) {
    private val real = FloatArray(n)
    private val imag = FloatArray(n)
    private val cosTable = FloatArray(n / 2)
    private val sinTable = FloatArray(n / 2)
    private val bitRev = IntArray(n)

    init {
        require(n > 1 && (n and (n - 1)) == 0) { "FFT size must be a power of two" }
        var j = 0
        for (i in 0 until n) {
            bitRev[i] = j
            var bit = n shr 1
            while (j and bit != 0) { j = j xor bit; bit = bit shr 1 }
            j = j xor bit
        }
        for (i in cosTable.indices) {
            val a = 2.0 * Math.PI * i / n
            cosTable[i] = kotlin.math.cos(a).toFloat()
            sinTable[i] = kotlin.math.sin(a).toFloat()
        }
    }

    fun magnitudeDb(input: ShortArray, count: Int, out: FloatArray) {
        val m = minOf(count, n)
        for (i in 0 until n) {
            real[i] = if (i < m) input[i] / 32768f * (0.5f - 0.5f * kotlin.math.cos(2f * Math.PI.toFloat() * i / maxOf(1, m - 1))) else 0f
            imag[i] = 0f
        }
        for (i in 0 until n) {
            val r = bitRev[i]
            if (r > i) { val t = real[i]; real[i] = real[r]; real[r] = t }
        }
        var len = 2
        while (len <= n) {
            val half = len shr 1
            val step = n / len
            var base = 0
            while (base < n) {
                for (j in 0 until half) {
                    val wr = cosTable[j * step]
                    val wi = -sinTable[j * step]
                    val k = base + j + half
                    val tr = real[k] * wr - imag[k] * wi
                    val ti = real[k] * wi + imag[k] * wr
                    val p = base + j
                    real[k] = real[p] - tr; imag[k] = imag[p] - ti
                    real[p] += tr; imag[p] += ti
                }
                base += len
            }
            len = len shl 1
        }
        val bins = minOf(out.size, n / 2)
        for (i in 0 until bins) {
            val mag = kotlin.math.sqrt(real[i] * real[i] + imag[i] * imag[i]) / maxOf(1, m)
            out[i] = (20f * kotlin.math.log10(maxOf(1e-6f, mag))).coerceIn(-100f, 6f)
        }
    }
}
