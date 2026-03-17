package com.looplingua.app.ui.track

import com.looplingua.app.domain.model.Segment

sealed class TrackListItem {

    data class SegmentItem(
        val segment: Segment,
        val segmentIndex: Int // ← これが超重要
    ) : TrackListItem()

    data class TrackHeader(
        val title: String
    ) : TrackListItem()
}