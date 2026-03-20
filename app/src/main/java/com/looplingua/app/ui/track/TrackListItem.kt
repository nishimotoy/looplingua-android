package com.looplingua.app.ui.track

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.SegmentKey

sealed class TrackListItem {

    data class TrackHeader(
        val title: String
    ) : TrackListItem()

    data class SegmentItem(
        val segment: Segment,
        val segmentIndex: Int,
        val key: SegmentKey
    ) : TrackListItem()
}