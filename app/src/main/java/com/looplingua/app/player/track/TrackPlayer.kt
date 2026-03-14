package com.looplingua.app.player.track
import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.player.segment.SegmentPlayer

class TrackPlayer(
    private val sequenceBuilder: SequenceBuilder,
    private val segmentPlayer: SegmentPlayer
) {

    private var track: Track? = null
    private var segments: List<Segment> = emptyList()
    private var currentSegmentIndex = 0

    fun setTrack(track: Track) {
        this.track = track
    }

    fun setSegments(segmentList: List<Segment>) {
        segments = segmentList
        currentSegmentIndex = 0
    }

    fun start() {
        val currentTrack = track ?: return
        if (segments.isEmpty()) return
        currentSegmentIndex = 0
        playCurrentSegment(currentTrack)
    }

    private fun playCurrentSegment(currentTrack: Track) {
        if (currentSegmentIndex >= segments.size) return
        val segment = segments[currentSegmentIndex]
        val steps = sequenceBuilder.build(
            track = currentTrack,
            segment = segment,
            pattern = Pattern.BASIC
        )
        segmentPlayer.play(steps) {
            currentSegmentIndex++
            playCurrentSegment(currentTrack)
        }
    }

    fun stop() {
        segmentPlayer.stop()
    }
}