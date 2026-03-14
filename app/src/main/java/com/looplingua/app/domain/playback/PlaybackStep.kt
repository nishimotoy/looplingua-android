package com.looplingua.app.domain.playback

data class PlaybackStep(

    val stepType: StepType,

    val audioResId: Int?,

    val startMs: Long?,
    val endMs: Long?,

    val pauseMs: Long
)