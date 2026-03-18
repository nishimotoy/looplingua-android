package com.looplingua.app.domain.playback

data class PatternDefinition(
    val name: String,
    val steps: List<StepType>
)