package com.looplingua.app.player.track

import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.sequence.SequenceBuilder

class TrackPlayer(
    private val sequenceBuilder: SequenceBuilder
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

        val segment = segments[currentSegmentIndex]

        val steps = sequenceBuilder.build(
            track = currentTrack,
            segment = segment,
            pattern = Pattern.BASIC
        )

        // TODO SegmentPlayerで再生
    }

    fun stop() {

    }
}