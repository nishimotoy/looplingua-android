package com.looplingua.app.player.queue

import com.looplingua.app.domain.model.Segment
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

        // 次のTrackへ
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

        // 前のTrackへ
        if (currentTrackIndex > 0) {
            currentTrackIndex--
            loadCurrentTrack()

            // ★ 前のTrackの最後に行く
            while (segmentQueue.next() != null) {
                // 最後まで進める
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

    fun find(target: Segment): Segment? {

        tracks.forEachIndexed { trackIndex, track ->

            val segmentIndex = track.segments.indexOfFirst {
                it.id == target.id
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