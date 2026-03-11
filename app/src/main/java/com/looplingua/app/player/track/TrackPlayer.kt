package com.looplingua.app.player.track

import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.playback.StepType
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.player.segment.SegmentQueue

class TrackPlayer(
    private val sequenceBuilder: SequenceBuilder,
    private val segmentQueue: SegmentQueue
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

        segmentQueue.setSteps(steps)

        segmentQueue.start()
    }

    fun stop() {

        segmentQueue.stop()

    }
}