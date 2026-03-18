package com.looplingua.app.player.track

import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.player.segment.SegmentPlayer
import com.looplingua.app.domain.playback.Pattern

class TrackPlayer(
    private val sequenceBuilder: SequenceBuilder,
    private val segmentPlayer: SegmentPlayer
) {

    private var segments: List<Segment> = emptyList()
    private var track: Track? = null
    private var currentIndex = 0

    fun load(track: Track, segments: List<Segment>) {
        this.track = track
        this.segments = segments
        currentIndex = 0
    }

    fun start(onTrackComplete: () -> Unit) {
        playNext(onTrackComplete)
    }

    private fun playNext(onTrackComplete: () -> Unit) {

        if (currentIndex >= segments.size) {
            onTrackComplete()
            return
        }

        val segment = segments[currentIndex]
        val steps = sequenceBuilder.build(
            track = track!!,
            segment = segment,
            pattern = Pattern.BASIC
        )

        segmentPlayer.play(steps) {
            currentIndex++
            playNext(onTrackComplete)
        }
    }

    fun stop() {
        segmentPlayer.stop()
    }
}