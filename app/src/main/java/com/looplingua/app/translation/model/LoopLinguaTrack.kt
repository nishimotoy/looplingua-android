package com.looplingua.app.translation.model

data class LoopLinguaTrack(
    val trackId: Int,
    val fileName: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val segments: List<LoopLinguaSegment>
)