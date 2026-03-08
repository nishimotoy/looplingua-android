package com.looplingua.app.player.segment

import android.os.Handler
import android.os.Looper
import com.looplingua.app.player.audio.AudioPlayer

class SegmentPlayer(
    private val audioPlayer: AudioPlayer
) {

    private val handler = Handler(Looper.getMainLooper())

    private var monitorRunnable: Runnable? = null

    fun playSegment(
        startMs: Long,
        endMs: Long,
        onFinished: () -> Unit
    ) {

        audioPlayer.seekTo(startMs)
        audioPlayer.play()

        startMonitoring(endMs, onFinished)
    }

    private fun startMonitoring(
        endMs: Long,
        onFinished: () -> Unit
    ) {

        monitorRunnable?.let { handler.removeCallbacks(it) }

        monitorRunnable = object : Runnable {

            override fun run() {

                val pos = audioPlayer.currentPosition()

                if (pos >= endMs - 20) {

                    audioPlayer.pause()

                    onFinished()

                } else {

                    handler.postDelayed(this, 10)
                }
            }
        }

        handler.post(monitorRunnable!!)
    }

    fun stop() {

        audioPlayer.pause()

        monitorRunnable?.let {
            handler.removeCallbacks(it)
        }
    }
}