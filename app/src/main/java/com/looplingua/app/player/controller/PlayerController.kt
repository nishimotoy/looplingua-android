package com.looplingua.app.player.controller

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.SegmentKey
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
    private val _currentKey = MutableStateFlow<SegmentKey?>(null)
    val currentKey: StateFlow<SegmentKey?> = _currentKey.asStateFlow()

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

    fun playFrom(key: SegmentKey) {

        val found = queue.findByKey(key) ?: return

        _isPlaying.value = true
        updateState()
        playSegment(found)
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

            if (pinnedKey != null) {

                val pinned = queue.findByKey(pinnedKey!!)
                if (pinned != null) {
                    playSegment(pinned)
                }

            } else {

                val next = queue.nextSegment()

                if (next != null) {
                    playSegment(next)
                } else {
                    val restart = queue.rewindToStart()

                    if (restart != null) {
                        playSegment(restart)
                    } else {
                        _isPlaying.value = false
                    }
                }
            }
        }
    }

    private fun updateState() {
        val track = queue.currentTrack()
        val segment = queue.currentSegment()

        _currentTrack.value = track
        _currentSegment.value = segment

        _currentKey.value = if (track != null && segment != null) {
            SegmentKey(track.track.id, segment.id)
        } else {
            null
        }
    }

    // pin
    private var pinnedKey: SegmentKey? = null

    fun togglePin() {
        val current = _currentKey.value ?: return

        pinnedKey = if (pinnedKey == current) {
            null // 解除
        } else {
            current // 設定
        }
    }

    fun isPinned(): Boolean {
        return pinnedKey != null && pinnedKey == _currentKey.value
    }
}