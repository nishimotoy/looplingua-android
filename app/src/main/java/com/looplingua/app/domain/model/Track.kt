package com.looplingua.app.domain.model

data class Track(
    val id: String,
    val name: String,
    val segmentIds: List<String>
)
