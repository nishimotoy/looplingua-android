package com.looplingua.app.player.controller

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.queue.SegmentQueue
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.player.segment.SegmentPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerController(
    private val track: Track,
    private val queue: SegmentQueue,
    private val sequenceBuilder: SequenceBuilder,
    private val segmentPlayer: SegmentPlayer
) {

    private var pattern: Pattern = Pattern.BASIC

    // 現在セグメント
    private val _currentSegment = MutableStateFlow<Segment?>(null)
    val currentSegment: StateFlow<Segment?> = _currentSegment.asStateFlow()

    // 再生状態
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // 現在インデックス
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    fun setPattern(newPattern: Pattern) {
        pattern = newPattern

        if (_isPlaying.value) {
            stop()
            play()
        }
    }

    fun setSegments(segments: List<Segment>) {
        queue.setSegments(segments)
        _currentIndex.value = 0
    }

    fun play() {
        if (_isPlaying.value) return

        val segment = queue.current() ?: return

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
        val nextSegment = queue.next() ?: return
        _currentIndex.value = queue.getCurrentIndex()
        playSegment(nextSegment)
    }

    fun prev() {
        val segment = queue.prev() ?: return
        _currentIndex.value = queue.getCurrentIndex()
        playSegment(segment)
    }

    fun getCurrentIndex(): Int {
        return queue.getCurrentIndex()
    }

    fun playFrom(index: Int) {
        val segment = queue.jumpTo(index) ?: return
        _currentIndex.value = index
        playSegment(segment)
    }

    private fun playSegment(segment: Segment) {

        segmentPlayer.stop()

        // UI更新
        _currentSegment.value = segment
        _currentIndex.value = queue.getCurrentIndex()

        val steps = sequenceBuilder.build(
            track = track,
            segment = segment,
            pattern = pattern
        )

        segmentPlayer.play(steps) {

            if (!_isPlaying.value) return@play

            val nextSegment = queue.next()

            if (nextSegment != null) {
                playSegment(nextSegment)
            } else {
                _isPlaying.value = false
            }
        }
    }
}