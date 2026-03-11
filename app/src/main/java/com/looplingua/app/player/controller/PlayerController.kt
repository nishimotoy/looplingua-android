package com.looplingua.app.player.controller

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.playlist.SegmentPlaylist
import com.looplingua.app.player.segment.SegmentQueue
import com.looplingua.app.player.sequence.SequenceBuilder

class PlayerController(
    private val track: Track,
    private val playlist: SegmentPlaylist,
    private val sequenceBuilder: SequenceBuilder,
    private val segmentQueue: SegmentQueue,
    private val pattern: Pattern
) {

    private var isPlaying = false

    fun play() {

        if (isPlaying) return

        val segment = playlist.currentSegment() ?: return

        isPlaying = true

        playSegment(segment)
    }

    private fun playSegment(segment: Segment) {

        val steps = sequenceBuilder.build(
            track = track,
            segment = segment,
            pattern = Pattern.BASIC
        )

        segmentQueue.setSteps(steps)

        segmentQueue.start {

            onSegmentFinished()

        }
    }

    private fun onSegmentFinished() {

        if (!playlist.hasNext()) {
            isPlaying = false
            return
        }

        val next = playlist.nextSegment() ?: return

        playSegment(next)
    }

    fun stop() {

        isPlaying = false

        segmentQueue.stop()
    }
}