package com.looplingua.app.ui.mapper

import com.looplingua.app.domain.model.SegmentKey
import com.looplingua.app.domain.model.TrackWithSegments
import com.looplingua.app.ui.track.TrackListItem

object TrackUiMapper {

    fun buildItems(
        tracks: List<TrackWithSegments>
    ): List<TrackListItem> {

        val items = mutableListOf<TrackListItem>()
        var index = 0

        tracks.forEach { data ->

            items.add(
                TrackListItem.TrackHeader(
                    data.track.title
                )
            )

            data.segments.forEach { segment ->
                items.add(
                    TrackListItem.SegmentItem(
                        segment = segment,
                        segmentIndex = index,
                        key = SegmentKey(
                            trackId = data.track.id,
                            segmentId = segment.id
                        )
                    )
                )
                index++
            }
        }

        return items
    }
}