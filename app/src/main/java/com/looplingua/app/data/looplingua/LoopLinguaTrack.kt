package com.looplingua.app.data.looplingua

import kotlinx.serialization.Serializable

@Serializable
data class LoopLinguaTrack(
    val trackId: Int,
    val fileName: String,
    val originalLang: String,
    val translationLang: String,
    val segments: List<LoopLinguaSegment>
)