package com.looplingua.app.domain.model

data class Segment(

    val id: String,

    val startMs: Int,

    val endMs: Int,

    val translation: String? = null,

    val memo: String? = null
)