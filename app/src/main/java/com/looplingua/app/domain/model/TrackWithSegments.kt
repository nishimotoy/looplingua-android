package com.looplingua.app.domain.model

data class TrackWithSegments(
    val track: Track,
    val segments: List<Segment>
)