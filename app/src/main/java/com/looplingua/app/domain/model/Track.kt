package com.looplingua.app.domain.model

data class Track(

    val id: String,

    val title: String,

    val audioRes: Int,

    val segments: List<Segment>
)