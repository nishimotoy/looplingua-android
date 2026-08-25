package com.looplingua.app.player.audio

object AudioPlayerManager {

    private val players =
        mutableSetOf<AudioPlayer>()

    fun register(player: AudioPlayer) {
        players += player
    }

    fun unregister(player: AudioPlayer) {
        players -= player
    }

    fun stopAll() {
        players.toList().forEach { player ->
            player.stop()
        }
    }
}