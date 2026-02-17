package com.looplingua.app.domain.model

data class Playlist(
    val id: String,
    val name: String,
    val trackIds: List<String>
)
