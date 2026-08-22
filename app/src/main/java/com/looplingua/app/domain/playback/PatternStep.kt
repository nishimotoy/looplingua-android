package com.looplingua.app.domain.playback

data class PatternStep(
    val type: StepType,
    val multiplier: Float = 1.0f
)