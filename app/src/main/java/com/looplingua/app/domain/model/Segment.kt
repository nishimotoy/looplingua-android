package com.looplingua.app.domain.model

data class Segment(
    val id: String,
    val sourceMediaId: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val originalText: String,
    val translationText: String? = null
)
