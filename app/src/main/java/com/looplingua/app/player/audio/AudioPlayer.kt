package com.looplingua.app.player.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

class AudioPlayer(private val context: Context) {

    private val player = ExoPlayer.Builder(context).build()

    // MediaItem キャッシュ
    private val mediaItemCache = mutableMapOf<Int, MediaItem>()

    private fun getMediaItem(audioRes: Int): MediaItem {

        return mediaItemCache.getOrPut(audioRes) {

            val uri = "android.resource://${context.packageName}/$audioRes"

            MediaItem.fromUri(uri)
        }
    }

    fun playSegment(
        audioRes: Int,
        startMs: Int,
        endMs: Int
    ) {

        val mediaItem = getMediaItem(audioRes)

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