package com.stylo.dsp

/** Optional platform bridge. The app uses the custom DSP path by default. */
class NativeEqBridge(private val audioSessionId: Int, private val bandCount: Int = 31) {
    var available: Boolean = false
        private set

    fun setGraphicGain(index: Int, gainDb: Float) {
        if (index !in 0 until bandCount) return
        // Reserved for an optional platform-specific implementation.
    }

    fun setBypass(bypass: Boolean) {
        // Reserved for an optional platform-specific implementation.
    }

    fun release() {
        // No native resources in the custom DSP implementation.
    }
}
