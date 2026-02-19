package com.looplingua.app.service.playback

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player

class SimpleSegmentPlayer(
    private val context: Context
) {

    private val player = ExoPlayer.Builder(context).build()
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false

    fun playSegment(
        @RawRes resId: Int,
        startMs: Long,
        endMs: Long
    ) {
        val uri = "android.resource://${context.packageName}/$resId"
        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)
        player.prepare()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    player.seekTo(startMs)
                    player.play()
                    startPositionMonitoring(endMs)
                    player.removeListener(this)
                }
            }
        })
    }

    private fun startPositionMonitoring(endMs: Long) {

        isMonitoring = true

        val checkRunnable = object : Runnable {
            override fun run() {

                if (!isMonitoring) return

                if (player.currentPosition >= endMs) {
                    player.pause()
                    isMonitoring = false
                } else {
                    handler.postDelayed(this, 10)
                }
            }
        }

        handler.post(checkRunnable)
    }

    fun release() {
        player.release()
    }
}
