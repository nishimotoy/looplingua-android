package com.looplingua.app.player.audio

import android.util.Log

object AudioPlayerManager {

    private val players =
        mutableSetOf<AudioPlayer>()

    fun register(player: AudioPlayer) {
        players += player
        Log.d(
            "PLAYER_TRACE",
            "AudioPlayerManager.register() " +
                    "count=${players.size}"
        )
    }

    fun unregister(player: AudioPlayer) {
        players -= player
        Log.d(
            "PLAYER_TRACE",
            "AudioPlayerManager.unregister() " +
                    "count=${players.size}"
        )
    }

    fun stopAll() {
        Log.d(
            "PLAYER_TRACE",
            "AudioPlayerManager.stopAll() " +
                    "count=${players.size}"
        )

        players.toList().forEach { player ->
            player.stop()
        }
    }
}