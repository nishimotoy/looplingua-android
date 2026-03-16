package com.looplingua.app.domain.model

data class AudioSlice(

    val audioPath: String,
    val startMs: Long,
    val endMs: Long
)