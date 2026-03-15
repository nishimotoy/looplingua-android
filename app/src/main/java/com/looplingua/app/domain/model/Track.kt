package com.looplingua.app.domain.model

data class Track(
    val id: Long,
    val originalAudioPath: String,
    val translationAudioPath: String?,
    val memoAudioPath: String?
)