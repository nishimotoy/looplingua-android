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
            return segmentQueue.jumpTo(0)
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

    fun currentTrack(): TrackWithSegments? {
        return tracks.getOrNull(currentTrackIndex)
    }

    fun allTracks(): List<TrackWithSegments> {
        return tracks
    }

    // SegmentKeyベース検索
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

    /**
     * 指定したSegmentを更新する。
     *
     * 今回は主にflaggedの変更に使用する。
     *
     * @return 更新後のSegment。対象が見つからなければnull。
     */
    fun updateSegment(
        key: SegmentKey,
        update: (Segment) -> Segment
    ): Segment? {

        // 1. 対象トラックを探す
        val trackIndex = tracks.indexOfFirst {
            it.track.id == key.trackId
        }

        if (trackIndex == -1) return null

        val track = tracks[trackIndex]

        // 2. 対象セグメントを探す
        val segmentIndex = track.segments.indexOfFirst {
            it.id == key.segmentId
        }

        if (segmentIndex == -1) return null

        // 3. セグメントを更新
        val currentSegment = track.segments[segmentIndex]
        val updatedSegment = update(currentSegment)

        // 4. セグメントリストを更新
        val updatedSegments = track.segments.toMutableList().apply {
            this[segmentIndex] = updatedSegment
        }

        val updatedTrack = track.copy(
            segments = updatedSegments
        )

        // 5. トラックリストを更新
        tracks = tracks.toMutableList().apply {
            this[trackIndex] = updatedTrack
        }

        // 6. 現在トラックなら SegmentQueue にも反映
        if (trackIndex == currentTrackIndex) {
            segmentQueue.setSegments(updatedTrack.segments)
            segmentQueue.jumpTo(segmentIndex)
        }

        return updatedSegment
    }

    fun rewindToStart(): Segment? {
        if (tracks.isEmpty()) return null

        currentTrackIndex = 0
        loadCurrentTrack()
        return segmentQueue.current()
    }
}