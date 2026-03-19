package com.looplingua.app.player.controller

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.TrackWithSegments
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.queue.TrackQueue
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.player.segment.SegmentPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerController(
    private val queue: TrackQueue,
    private val sequenceBuilder: SequenceBuilder,
    private val segmentPlayer: SegmentPlayer
) {

    private var pattern: Pattern = Pattern.BASIC

    // 現在セグメント
    private val _currentSegment = MutableStateFlow<Segment?>(null)
    val currentSegment: StateFlow<Segment?> = _currentSegment.asStateFlow()

    // 現在トラック
    private val _currentTrack = MutableStateFlow<TrackWithSegments?>(null)
    val currentTrack: StateFlow<TrackWithSegments?> = _currentTrack.asStateFlow()

    // 再生状態
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // インデックス
    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _currentSegmentIndex = MutableStateFlow(0)
    val currentSegmentIndex: StateFlow<Int> = _currentSegmentIndex.asStateFlow()

    fun setPattern(newPattern: Pattern) {
        pattern = newPattern

        if (_isPlaying.value) {
            stop()
            play()
        }
    }

    fun setTracks(tracks: List<TrackWithSegments>) {
        queue.setTracks(tracks)
        updateState()
    }

    fun play() {
        if (_isPlaying.value) return

        val segment = queue.currentSegment() ?: return

        _isPlaying.value = true
        playSegment(segment)
    }

    fun stop() {
        if (!_isPlaying.value) return

        _isPlaying.value = false
        segmentPlayer.stop()
    }

    fun togglePlay() {
        if (_isPlaying.value) stop() else play()
    }

    fun next() {
        val next = queue.nextSegment() ?: return
        updateState()
        playSegment(next)
    }

    fun prev() {
        val prev = queue.prevSegment() ?: return
        updateState()
        playSegment(prev)
    }

    fun playFrom(trackIndex: Int, segmentIndex: Int) {
        val segment = queue.jumpTo(trackIndex, segmentIndex) ?: return
        updateState()
        playSegment(segment)
    }

    private fun playSegment(segment: Segment) {

        segmentPlayer.stop()

        updateState()

        val currentTrack = queue.currentTrack() ?: return

        val steps = sequenceBuilder.build(
            track = currentTrack.track,
            segment = segment,
            pattern = pattern
        )

        segmentPlayer.play(steps) {

            if (!_isPlaying.value) return@play

            val next = queue.nextSegment()

            if (next != null) {
                playSegment(next)
            } else {
                _isPlaying.value = false
            }
        }
    }

    private fun updateState() {
        _currentSegment.value = queue.currentSegment()
        _currentTrack.value = queue.currentTrack()
        _currentTrackIndex.value = queue.getCurrentTrackIndex()
        _currentSegmentIndex.value = queue.getCurrentSegmentIndex()
    }
}