package com.looplingua.app.player.audio

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayer(context: Context) {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    fun load(uri: String) {
        val mediaItem = MediaItem.fromUri(Uri.parse(uri))
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    fun currentPosition(): Long {
        return player.currentPosition
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun release() {
        player.release()
    }
}