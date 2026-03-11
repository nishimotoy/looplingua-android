package com.looplingua.app.player.controller

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.segment.SegmentPlaylist
import com.looplingua.app.player.segment.SegmentQueue
import com.looplingua.app.player.sequence.SequenceBuilder

class PlayerController(

    private val track: Track,
    private val playlist: SegmentPlaylist,
    private val sequenceBuilder: SequenceBuilder,
    private val segmentQueue: SegmentQueue

) {

    private var pattern: Pattern = Pattern.BASIC


    init {

        segmentQueue.setOnFinishedListener {

            playlist.next { segment ->

                playSegment(segment)

            }
        }
    }


    fun setSegments(segments: List<Segment>) {

        playlist.setSegments(segments)
    }


    fun setPattern(newPattern: Pattern) {

        pattern = newPattern
    }


    fun play() {

        playlist.start { segment ->

            playSegment(segment)

        }
    }


    fun stop() {

        segmentQueue.stop()
    }


    private fun playSegment(segment: Segment) {

        val steps = sequenceBuilder.build(

            track = track,
            segment = segment,
            pattern = pattern
        )

        segmentQueue.setSteps(steps)

        segmentQueue.start()
    }
}