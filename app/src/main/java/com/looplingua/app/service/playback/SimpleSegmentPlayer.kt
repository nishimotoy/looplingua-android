package com.looplingua.app.service.playback

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class SimpleSegmentPlayer(
    private val context: Context
) {

    private val player = ExoPlayer.Builder(context).build()
    private val handler = Handler(Looper.getMainLooper())

    fun playSegment(
        @RawRes resId: Int,
        startMs: Long,
        endMs: Long
    ) {
        val uri = "android.resource://${context.packageName}/$resId"

        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()

        player.seekTo(startMs)
        player.play()

        handler.postDelayed({
            player.pause()
        }, endMs - startMs)
    }

    fun release() {
        player.release()
    }
}
