package com.looplingua.app.domain.playback

import com.looplingua.app.domain.model.AudioSlice

data class PlaybackStep(

    val stepType: StepType,
    val slice: AudioSlice?,
    val pauseMs: Long
)