package com.looplingua.app.player.controller

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.segment.SegmentPlaylist
import com.looplingua.app.player.segment.SegmentQueue
import com.looplingua.app.player.sequence.SequenceBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerController(

    private val track: Track,
    private val playlist: SegmentPlaylist,
    private val sequenceBuilder: SequenceBuilder,
    private val segmentQueue: SegmentQueue

) {

    private var pattern: Pattern = Pattern.BASIC

    // UI通知用
    private val _currentSegment = MutableStateFlow<Segment?>(null)
    val currentSegment: StateFlow<Segment?> = _currentSegment


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

        _currentSegment.value = null
    }


    private fun playSegment(segment: Segment) {

        // ★ UIへ通知
        _currentSegment.value = segment

        val steps = sequenceBuilder.build(
            track = track,
            segment = segment,
            pattern = pattern
        )

        segmentQueue.setSteps(steps)

        segmentQueue.start()
    }
}