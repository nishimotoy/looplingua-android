package com.looplingua.app.domain.model

data class Segment(
    val id: String,
    val startMs: Long,
    val endMs: Long,
    val originalText: String,
    val translationAutoText: String?,
    val translationUserText: String?
) {

    val durationMs: Long
        get() = endMs - startMs
}