package com.looplingua.app.domain.playback

data class PlaybackConfig(
    val repeatCount: Int,
    val pauseMultiplier: Float,
    val autoNextTrack: Boolean
)
