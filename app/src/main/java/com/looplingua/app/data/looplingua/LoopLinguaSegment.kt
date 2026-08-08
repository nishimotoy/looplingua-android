package com.looplingua.app.data.looplingua

import kotlinx.serialization.Serializable

@Serializable
data class LoopLinguaSegment(
    val segmentId: Int,

    val originalStartMs: Long,
    val originalEndMs: Long,

    val originalAuto: String,
    val originalUser: String = "",

    val translationAuto: String = "",
    val translationUser: String = "",

    val memoUser: String = ""
)