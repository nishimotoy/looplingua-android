package com.looplingua.app.domain.playback

data class PlaybackStep(
    val segmentId: String,
    val startMs: Long,
    val endMs: Long,
    val stepType: StepType,
    val audioSource: AudioSource
) {

    val durationMs: Long
        get() = endMs - startMs
}