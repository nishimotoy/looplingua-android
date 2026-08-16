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
    private val segmentPlayer: SegmentPlayer,
    private val saveFlags: (List<TrackWithSegments>) -> Unit
) {

    private var pattern: Pattern = Pattern.BASIC

    // ============================================================
    // Current Segment
    // ============================================================

    private val _currentSegment =
        MutableStateFlow<Segment?>(null)

    val currentSegment: StateFlow<Segment?> =
        _currentSegment.asStateFlow()

    // ============================================================
    // Current Track
    // ============================================================

    private val _currentTrack =
        MutableStateFlow<TrackWithSegments?>(null)

    // ============================================================
    // Playing State
    // ============================================================

    private val _isPlaying =
        MutableStateFlow(false)

    val isPlaying: StateFlow<Boolean> =
        _isPlaying.asStateFlow()

    // ============================================================
    // Current Key
    // ============================================================

    private val _currentKey =
        MutableStateFlow<SegmentKey?>(null)

    val currentKey: StateFlow<SegmentKey?> =
        _currentKey.asStateFlow()

    // ============================================================
    // Pin
    // ============================================================

    private val _pinnedKey =
        MutableStateFlow<SegmentKey?>(null)

    val pinnedKey: StateFlow<SegmentKey?> =
        _pinnedKey.asStateFlow()

    // ============================================================
    // Pattern
    // ============================================================

    fun setPattern(newPattern: Pattern) {

        pattern = newPattern

        if (_isPlaying.value) {
            stop()
            play()
        }
    }

    // ============================================================
    // Tracks
    // ============================================================

    fun setTracks(
        tracks: List<TrackWithSegments>
    ) {

        queue.setTracks(tracks)
        updateState()
    }

    // ============================================================
    // Playback
    // ============================================================

    fun play() {

        if (_isPlaying.value) return

        val segment =
            queue.currentSegment() ?: return

        _isPlaying.value = true
        playSegment(segment)
    }

    fun stop() {

        if (!_isPlaying.value) return

        _isPlaying.value = false
        segmentPlayer.stop()
    }

    fun togglePlay() {

        if (_isPlaying.value) {
            stop()
        } else {
            play()
        }
    }

    fun next() {

        val next =
            queue.nextSegment() ?: return

        updateState()
        playSegment(next)
    }

    fun prev() {

        val prev =
            queue.prevSegment() ?: return

        updateState()
        playSegment(prev)
    }

    fun playFrom(key: SegmentKey) {

        val found =
            queue.findByKey(key) ?: return

        _isPlaying.value = true

        updateState()
        playSegment(found)
    }

    private fun playSegment(segment: Segment) {

        segmentPlayer.stop()

        updateState()

        val currentTrack =
            queue.currentTrack() ?: return

        val steps =
            sequenceBuilder.build(
                track = currentTrack.track,
                segment = segment,
                pattern = pattern
            )

        segmentPlayer.play(steps) {

            if (!_isPlaying.value) return@play

            if (_pinnedKey.value != null) {

                val pinned =
                    queue.findByKey(_pinnedKey.value!!)

                if (pinned != null) {
                    playSegment(pinned)
                }

            } else {

                val next =
                    queue.nextSegment()

                if (next != null) {

                    playSegment(next)

                } else {

                    val restart =
                        queue.rewindToStart()

                    if (restart != null) {
                        playSegment(restart)
                    } else {
                        _isPlaying.value = false
                    }
                }
            }
        }
    }

    // ============================================================
    // State
    // ============================================================

    private fun updateState() {

        val track =
            queue.currentTrack()

        val segment =
            queue.currentSegment()

        _currentTrack.value = track
        _currentSegment.value = segment

        _currentKey.value =
            if (track != null && segment != null) {

                SegmentKey(
                    track.track.id,
                    segment.id
                )

            } else {
                null
            }
    }

    // ============================================================
    // Pin
    // ============================================================

    fun togglePin() {

        val current =
            _currentKey.value ?: return

        _pinnedKey.value =
            if (_pinnedKey.value == current) {
                null
            } else {
                current
            }
    }

    fun isPinned(): Boolean {

        return _pinnedKey.value != null &&
                _pinnedKey.value == _currentKey.value
    }

    // ============================================================
    // Flag
    // ============================================================

    fun toggleFlag() {

        val current =
            _currentKey.value ?: return

        val updatedSegment =
            queue.updateSegment(current) {

                it.copy(
                    flagged = !it.flagged
                )
            } ?: return

        _currentSegment.value =
            updatedSegment

        // フラグ変更直後に永続保存する。
        // 実際の保存処理はUIスレッドをブロックしない。
        saveFlags(queue.allTracks())
    }

    fun isFlagged(): Boolean {

        return _currentSegment.value?.flagged == true
    }
}