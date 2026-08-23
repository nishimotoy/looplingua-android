package com.looplingua.app.domain.playback

data class PatternDefinition(
    val name: String,
    val steps: List<PatternStep>,
    val playbackSpeed: Float = 1.0f
)