package com.looplingua.app.player.controller

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.segment.SegmentPlaylist
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.player.segment.SegmentPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerController(

    private val track: Track,
    private val playlist: SegmentPlaylist,
    private val sequenceBuilder: SequenceBuilder,
    private val segmentPlayer: SegmentPlayer

) {

    private var pattern: Pattern = Pattern.BASIC

    private val _currentSegment = MutableStateFlow<Segment?>(null)
    val currentSegment: StateFlow<Segment?> = _currentSegment


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
        segmentPlayer.stop()
    }

    fun next() {
        playlist.next { segment ->
            playSegment(segment)
        }
    }

    fun prev() {
        playlist.prev { segment ->
            playSegment(segment)
        }
    }

    fun getCurrentIndex(): Int {
        return playlist.getCurrentIndex()
    }

    fun playFrom(index: Int) {
        playlist.jumpTo(index) { segment ->
            playSegment(segment)
        }
    }

    fun togglePlay() {
        if (_currentSegment.value == null) {
            play()
        } else {
            stop()
        }
    }

    private fun playSegment(segment: Segment) {

        // ★ 二重再生防止
        segmentPlayer.stop()

        // ★ UIへ通知
        _currentSegment.value = segment

        val steps = sequenceBuilder.build(
            track = track,
            segment = segment,
            pattern = pattern
        )

        segmentPlayer.play(steps) {
            playlist.next { nextSegment ->
                playSegment(nextSegment)
            }
        }
    }
}