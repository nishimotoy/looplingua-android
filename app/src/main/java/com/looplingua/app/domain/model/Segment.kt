package com.looplingua.app.domain.model

data class Segment(

    val id: Long,

    val originalStartMs: Long,
    val originalEndMs: Long,

    val translationStartMs: Long?,
    val translationEndMs: Long?,

    val memoStartMs: Long?,
    val memoEndMs: Long?,

    val originalText: String,
    val translationText: String?,
    val memoText: String?
)