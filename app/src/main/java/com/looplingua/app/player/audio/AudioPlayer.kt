package com.looplingua.app.player.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

class AudioPlayer(private val context: Context) {

    private val player = ExoPlayer.Builder(context).build()

    fun playSegment(
        audioRes: Int,
        startMs: Int,
        endMs: Int
    ) {

        val uri = "android.resource://${context.packageName}/$audioRes"

        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)

        player.prepare()

        player.seekTo(startMs.toLong())

        player.play()
    }

    suspend fun waitUntil(endMs: Int) {

        while (player.currentPosition < endMs) {

            delay(20)

        }

        player.pause()
    }

    suspend fun pause(ms: Long) {

        delay(ms)

    }

    fun release() {

        player.release()

    }
}