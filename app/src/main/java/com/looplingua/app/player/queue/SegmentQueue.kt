package com.looplingua.app.player.queue

import com.looplingua.app.domain.model.Segment

class SegmentQueue {

    private var segments: List<Segment> = emptyList()
    private var currentSegmentIndex = 0

    fun setSegments(segmentList: List<Segment>) {
        segments = segmentList
        currentSegmentIndex = 0
    }

    fun current(): Segment? {
        return segments.getOrNull(currentSegmentIndex)
    }

    fun next(): Segment? {
        if (segments.isEmpty()) return null
        if (currentSegmentIndex >= segments.size - 1) return null

        currentSegmentIndex++
        return segments[currentSegmentIndex]
    }

    fun prev(): Segment? {
        if (segments.isEmpty()) return null

        if (currentSegmentIndex <= 0) {
            return segments.getOrNull(currentSegmentIndex)
        }

        currentSegmentIndex--
        return segments[currentSegmentIndex]
    }

    fun jumpTo(index: Int): Segment? {
        if (index !in segments.indices) return null

        currentSegmentIndex = index
        return segments[currentSegmentIndex]
    }

    fun getCurrentIndex(): Int {
        return currentSegmentIndex
    }
}