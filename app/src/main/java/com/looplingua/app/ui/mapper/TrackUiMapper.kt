package com.looplingua.app.ui.mapper

import com.looplingua.app.data.SegmentFile
import com.looplingua.app.ui.track.TrackListItem

object TrackUiMapper {

    fun buildItems(trackDataList: List<SegmentFile>): List<TrackListItem> {

        val items = mutableListOf<TrackListItem>()
        var index = 0

        trackDataList.forEach { data ->

            items.add(
                TrackListItem.TrackHeader(
                    data.track.title ?: "Track"
                )
            )

            data.segments.forEach { segment ->
                items.add(
                    TrackListItem.SegmentItem(
                        segment = segment,
                        segmentIndex = index
                    )
                )
                index++
            }
        }

        return items
    }
}