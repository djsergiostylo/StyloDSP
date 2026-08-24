package com.stylo.dsp

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

class EqModel {
    enum class Type { PEAK, LOW_SHELF, HIGH_SHELF, LOW_PASS, HIGH_PASS, NOTCH, BAND_PASS, ALL_PASS, TILT }
    data class Band(var freq: Double, var gain: Float = 0f, var q: Float = 1f, var enabled: Boolean = true, var type: Type = Type.PEAK)
    val graphic = MutableList(31) { i -> Band(20.0 * 1000.0.pow(i / 30.0)) }
    val parametric = MutableList(8) { i -> Band(doubleArrayOf(60.0,120.0,250.0,500.0,1000.0,2500.0,8000.0,14000.0)[i]) }
    var selected = 15
    var parametricMode = false
    var bypass = false
    var ab = false
    val active: MutableList<Band> get() = if (parametricMode) parametric else graphic
    fun nearestBand(freq: Double): Int { var best=0;var d=Double.MAX_VALUE;active.forEachIndexed{i,b->val x=kotlin.math.abs(ln(freq/b.freq));if(x<d){d=x;best=i}};return best }
    fun responseDb(freq: Double): Double {
        if(bypass||ab)return 0.0;var sum=0.0
        for(b in active)if(b.enabled&&b.gain!=0f){val ratio=freq/b.freq
            when(b.type){
                Type.PEAK->{val z=ln(ratio)/ln(2.0);val width=if(parametricMode)1.0/b.q.coerceAtLeast(.25f) else .42;sum+=b.gain*exp(-.5*(z/width)*(z/width))}
                Type.LOW_SHELF->sum+=b.gain/(1.0+ratio.pow(4.0));Type.HIGH_SHELF->sum+=b.gain/(1.0+(1.0/ratio).pow(4.0))
                Type.LOW_PASS->{if(freq>b.freq)sum-=minOf(24.0,24.0*ln(ratio)/ln(2.0)*b.q)};Type.HIGH_PASS->{if(freq<b.freq)sum-=minOf(24.0,24.0*ln(1.0/ratio)/ln(2.0)*b.q)}
                Type.NOTCH->{val z=ln(ratio)/ln(2.0);sum+=b.gain*(1.0-exp(-8.0*z*z))};Type.BAND_PASS->{val z=ln(ratio)/ln(2.0);sum+=b.gain*exp(-4.0*z*z)};Type.ALL_PASS->{};Type.TILT->sum+=b.gain*(ln(ratio)/ln(1000.0)).coerceIn(-1.0,1.0)
            }
        };return sum.coerceIn(-24.0,24.0)
    }
}
