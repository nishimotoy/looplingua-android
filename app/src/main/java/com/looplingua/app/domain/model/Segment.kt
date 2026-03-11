package com.looplingua.app.domain.model

data class Segment(

    val id: Long,

    val originalStartMs: Long,
    val originalEndMs: Long,

    val translationStartMs: Long = 0,
    val translationEndMs: Long = 0,

    val memoStartMs: Long = 0,
    val memoEndMs: Long = 0,

    val originalText: String,
    val translationText: String? = null,
    val memoText: String? = null
)