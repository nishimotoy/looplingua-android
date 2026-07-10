package com.looplingua.app.translation.model

data class LoopLinguaProject(
    val formatVersion: String = "1.0",
    val projectId: String,
    val projectName: String,
    val tracks: List<LoopLinguaTrack>
)