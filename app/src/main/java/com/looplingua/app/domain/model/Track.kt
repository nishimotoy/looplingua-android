package com.looplingua.app.domain.model

data class Track(

    val originalAudioPath: String,
    val translationAudioPath: String,
    val memoAudioPath: String,

    val segments: List<Segment>
)