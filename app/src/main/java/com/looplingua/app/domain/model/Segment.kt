package com.looplingua.app.domain.model

data class Segment(

    val id: String,

    val original: AudioSlice,
    val translation: AudioSlice?,
    val memo: AudioSlice?,

    val originalText: String,
    val translationText: String?,
    val memoText: String?
)