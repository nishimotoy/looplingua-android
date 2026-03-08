package com.looplingua.app.domain.model

data class Track(
    val id: String,
    val title: String,
    val audioFileUri: String,
    val durationMs: Long,
    val segments: List<Segment>
) {

    fun getSegmentById(segmentId: String): Segment? {
        return segments.find { it.id == segmentId }
    }

    fun segmentCount(): Int {
        return segments.size
    }
}