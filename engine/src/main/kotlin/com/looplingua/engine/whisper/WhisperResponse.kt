package com.looplingua.engine.whisper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WhisperResponse(

    val language: String,
    val duration: Double,
    val text: String,
    val segments: List<WhisperSegment>
)

@Serializable
data class WhisperSegment(

    val id: Int,

    val start: Double,

    val end: Double,

    val text: String
)