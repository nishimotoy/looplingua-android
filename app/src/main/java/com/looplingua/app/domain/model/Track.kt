package com.looplingua.app.domain.model

data class Track(

    val id: Long,

    val originalResId: Int,
    val translationResId: Int?,
    val memoResId: Int?
)