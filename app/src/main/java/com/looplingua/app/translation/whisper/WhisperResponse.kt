package com.looplingua.app.translation.whisper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WhisperResponse(

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