package com.looplingua.app.domain.model

data class Track(
    val id: Int,
    val title: String,
    val originalAudioPath: String,
    val translationAudioPath: String?,
    val memoAudioPath: String?
)