package com.looplingua.app.domain.model

data class SourceMedia(
    val id: String,
    val fileUri: String,
    val durationMs: Long,
    val title: String? = null
)
