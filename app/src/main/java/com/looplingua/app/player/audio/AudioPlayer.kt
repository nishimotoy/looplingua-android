package com.looplingua.app.player.audio

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayer(private val context: Context) {

    private val player = ExoPlayer.Builder(context).build()
    private val handler = Handler(Looper.getMainLooper())

    private var isMonitoring = false

    fun play(
        path: String,
        startMs: Long,
        endMs: Long,
        onComplete: () -> Unit
    ) {

        stop()

        val uri = Uri.parse(path)
        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)
        player.prepare()

        val listener = object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {

                if (state == Player.STATE_READY) {

                    player.seekTo(startMs)
                    player.play()

                    startMonitoring(endMs, onComplete)

                    player.removeListener(this)
                }
            }
        }

        player.addListener(listener)
    }

    private fun startMonitoring(
        endMs: Long,
        onComplete: () -> Unit
    ) {

        isMonitoring = true

        val runnable = object : Runnable {

            override fun run() {

                if (!isMonitoring) return

                if (player.currentPosition >= endMs) {

                    player.pause()
                    isMonitoring = false
                    onComplete()

                } else {

                    handler.postDelayed(this, 10)

                }
            }
        }

        handler.post(runnable)
    }

    fun stop() {

        isMonitoring = false
        handler.removeCallbacksAndMessages(null)

        player.stop()
        player.clearMediaItems()
    }

    fun release() {
        player.release()
    }
}