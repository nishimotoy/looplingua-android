package com.looplingua.app.player.audio

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayer(private val context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    fun play(
        resId: Int,
        startMs: Long,
        endMs: Long
    ) {

        val uri = Uri.parse("android.resource://${context.packageName}/$resId")

        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)
        player.prepare()

        player.seekTo(startMs)
        player.play()

        scheduleStop(endMs - startMs)
    }

    private fun scheduleStop(durationMs: Long) {

        Thread {

            Thread.sleep(durationMs)

            player.pause()

        }.start()
    }

    fun stop() {
        player.stop()
    }

    fun release() {
        player.release()
    }
}