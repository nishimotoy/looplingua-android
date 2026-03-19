package com.looplingua.app.player.segment

import com.looplingua.app.domain.model.Segment

class SegmentQueue {

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

        if (segments.isEmpty()) return

        if (currentSegmentIndex >= segments.size - 1) {
            return
        }

        currentSegmentIndex++

        playSegment(segments[currentSegmentIndex])
    }
    fun prev(playSegment: (Segment) -> Unit) {

        if (segments.isEmpty()) return

        if (currentSegmentIndex <= 0) {
            currentSegmentIndex = 0
            playSegment(segments[currentSegmentIndex])
            return
        }

        currentSegmentIndex--

        playSegment(segments[currentSegmentIndex])
    }
    fun stop() {

        currentSegmentIndex = 0
    }

    fun getCurrentSegment(): Segment? {

        if (segments.isEmpty()) return null

        return segments[currentSegmentIndex]
    }

    fun getCurrentIndex(): Int {
        return currentSegmentIndex
    }

    fun jumpTo(index: Int, playSegment: (Segment) -> Unit) {
        if (index < 0 || index >= segments.size) return
        currentSegmentIndex = index
        playSegment(segments[currentSegmentIndex])
    }
}