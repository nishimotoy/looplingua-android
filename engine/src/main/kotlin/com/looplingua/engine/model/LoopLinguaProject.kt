package com.looplingua.engine.model

import kotlinx.serialization.Serializable

@Serializable
data class LoopLinguaProject(
    val formatVersion: String = "1.0",
    val projectId: String,
    val projectName: String,
    val tracks: List<LoopLinguaTrack>
)