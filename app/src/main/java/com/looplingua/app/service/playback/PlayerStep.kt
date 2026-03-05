package com.looplingua.app.service.playback

sealed class PlayerStep {

    data class PlayOriginal(val startMs: Long, val endMs: Long) : PlayerStep()
    data class PlayTranslation(val startMs: Long, val endMs: Long) : PlayerStep()
    data class PlayMemo(val startMs: Long, val endMs: Long) : PlayerStep()
    data class Pause(val durationMs: Long) : PlayerStep()
}