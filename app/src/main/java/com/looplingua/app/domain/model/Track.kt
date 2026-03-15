package com.looplingua.app.domain.model

import androidx.appcompat.widget.DialogTitle

data class Track(
    val id: Long,
    val title: String?,
    val originalAudioPath: String,
    val translationAudioPath: String?,
    val memoAudioPath: String?
)