package com.looplingua.engine.model

import kotlinx.serialization.Serializable

@Serializable
data class LoopLinguaTrack(
    val trackId: Int,
    val fileName: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val segments: List<LoopLinguaSegment>
)