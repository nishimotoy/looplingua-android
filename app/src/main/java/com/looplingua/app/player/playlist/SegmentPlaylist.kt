package com.looplingua.app.player.segment

import com.looplingua.app.domain.model.Segment

class SegmentPlaylist(
    private val segmentQueue: SegmentQueue
) {

    private var segments: List<Segment> = emptyList()

    private var currentSegmentIndex = 0

    fun setSegments(segmentList: List<Segment>) {

        segments = segmentList
        currentSegmentIndex = 0
    }

    fun start(playSegment: (Segment) -> Unit) {

        if (segments.isEmpty()) return

        currentSegmentIndex = 0

        playSegment(segments[currentSegmentIndex])
    }

    fun next(playSegment: (Segment) -> Unit) {

        currentSegmentIndex++

        if (currentSegmentIndex >= segments.size) {
            return
        }

        playSegment(segments[currentSegmentIndex])
    }

    fun stop() {

        segmentQueue.stop()

        currentSegmentIndex = 0
    }

    fun getCurrentSegment(): Segment? {

        if (segments.isEmpty()) return null

        return segments[currentSegmentIndex]
    }
}