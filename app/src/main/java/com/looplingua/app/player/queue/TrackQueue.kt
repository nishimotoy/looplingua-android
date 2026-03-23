package com.looplingua.app.player.queue

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.SegmentKey
import com.looplingua.app.domain.model.TrackWithSegments

class TrackQueue {

    private var tracks: List<TrackWithSegments> = emptyList()
    private var currentTrackIndex = 0

    private val segmentQueue = SegmentQueue()

    fun setTracks(trackList: List<TrackWithSegments>) {
        tracks = trackList
        currentTrackIndex = 0
        loadCurrentTrack()
    }

    private fun loadCurrentTrack() {
        val current = tracks.getOrNull(currentTrackIndex) ?: return
        segmentQueue.setSegments(current.segments)
    }

    fun currentSegment(): Segment? {
        return segmentQueue.current()
    }

    fun nextSegment(): Segment? {
        val next = segmentQueue.next()
        if (next != null) return next

        if (currentTrackIndex < tracks.size - 1) {
            currentTrackIndex++
            loadCurrentTrack()
            return segmentQueue.current()
        }

        return null
    }

    fun prevSegment(): Segment? {
        val prev = segmentQueue.prev()
        if (prev != null) return prev

        if (currentTrackIndex > 0) {
            currentTrackIndex--
            loadCurrentTrack()

            while (segmentQueue.next() != null) {
                // 最後まで移動
            }

            return segmentQueue.current()
        }

        return null
    }

    fun jumpTo(trackIndex: Int, segmentIndex: Int): Segment? {
        if (trackIndex !in tracks.indices) return null

        currentTrackIndex = trackIndex
        loadCurrentTrack()

        return segmentQueue.jumpTo(segmentIndex)
    }

    fun getCurrentTrackIndex(): Int = currentTrackIndex

    fun getCurrentSegmentIndex(): Int = segmentQueue.getCurrentIndex()

    fun currentTrack(): TrackWithSegments? {
        return tracks.getOrNull(currentTrackIndex)
    }

    // ★ 修正：SegmentKeyベース検索
    fun findByKey(key: SegmentKey): Segment? {

        tracks.forEachIndexed { trackIndex, track ->

            if (track.track.id != key.trackId) return@forEachIndexed

            val segmentIndex = track.segments.indexOfFirst {
                it.id == key.segmentId
            }

            if (segmentIndex != -1) {
                currentTrackIndex = trackIndex
                loadCurrentTrack()
                return segmentQueue.jumpTo(segmentIndex)
            }
        }

        return null
    }
}