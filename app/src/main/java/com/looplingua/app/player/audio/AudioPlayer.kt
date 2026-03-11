package com.looplingua.app.player.audio

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayer(private val context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val handler = Handler(Looper.getMainLooper())

    fun play(
        resId: Int,
        startMs: Long,
        endMs: Long
    ) {

        // 前の再生を完全停止
        handler.removeCallbacksAndMessages(null)
        player.stop()

        val uri = Uri.parse("android.resource://${context.packageName}/$resId")
        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)
        player.prepare()

        player.seekTo(startMs)
        player.play()

        scheduleStop(endMs - startMs)
    }

    private fun scheduleStop(durationMs: Long) {

        handler.postDelayed({
            player.pause()
        }, durationMs)

    }

    fun stop() {
        handler.removeCallbacksAndMessages(null)
        player.stop()
    }

    fun release() {
        player.release()
    }
}