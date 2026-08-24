package com.stylo.dsp

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper

class PlayerEngine(private val context: Context, private val onState: (positionMs: Int, durationMs: Int, playing: Boolean) -> Unit) {
    private var player: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() {
            val p = player
            if (p != null) {
                val pos = runCatching { p.currentPosition }.getOrDefault(0)
                val dur = runCatching { p.duration }.getOrDefault(0)
                onState(pos, dur, p.isPlaying)
                if (p.isPlaying) handler.postDelayed(this, 250)
            }
        }
    }

    fun load(uri: Uri) {
        release()
        player = MediaPlayer().apply {
            setDataSource(context, uri)
            setOnPreparedListener { onState(0, duration, false) }
            setOnCompletionListener { seekTo(0); onState(0, duration, false) }
            setOnErrorListener { _, _, _ -> onState(0, 0, false); true }
            prepareAsync()
        }
    }

    fun playPause() {
        val p = player ?: return
        runCatching { if (p.isPlaying) p.pause() else p.start() }
        onState(runCatching { p.currentPosition }.getOrDefault(0), runCatching { p.duration }.getOrDefault(0), runCatching { p.isPlaying }.getOrDefault(false))
        handler.removeCallbacks(ticker)
        handler.post(ticker)
    }

    fun seekBy(deltaMs: Int) {
        val p = player ?: return
        runCatching { p.seekTo((p.currentPosition + deltaMs).coerceIn(0, p.duration.coerceAtLeast(0))) }
        onState(runCatching { p.currentPosition }.getOrDefault(0), runCatching { p.duration }.getOrDefault(0), runCatching { p.isPlaying }.getOrDefault(false))
    }

    fun seekTo(positionMs: Int) { runCatching { player?.seekTo(positionMs.coerceAtLeast(0)) } }
    fun setLoop(enabled: Boolean) { runCatching { player?.isLooping = enabled } }
    fun isLoaded(): Boolean = player != null
    fun audioSessionId(): Int = runCatching { player?.audioSessionId ?: 0 }.getOrDefault(0)
    fun release() { handler.removeCallbacks(ticker); player?.release(); player = null }
}
