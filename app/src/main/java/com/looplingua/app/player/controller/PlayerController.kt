package com.looplingua.app.player.controller

import com.looplingua.app.data.PlayerPreferences
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.SegmentKey
import com.looplingua.app.domain.model.TrackWithSegments
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.queue.TrackQueue
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.player.segment.SegmentPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerController(
    private val queue: TrackQueue,
    private val sequenceBuilder: SequenceBuilder,
    private val segmentPlayer: SegmentPlayer,
    private val saveFlags: (List<TrackWithSegments>) -> Unit,
    private val playerPreferences: PlayerPreferences
) {

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // ============================================================
    // Pattern
    // ============================================================

    private val _playbackPattern =
        MutableStateFlow(Pattern.BASIC)

    val playbackPattern: StateFlow<Pattern> =
        _playbackPattern.asStateFlow()

    init {

        scope.launch {

            playerPreferences.playbackPattern.collect { pattern ->

                _playbackPattern.value = pattern
            }
        }
    }

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

    val currentTrack: StateFlow<TrackWithSegments?> =
        _currentTrack.asStateFlow()

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

    fun setPattern(
        newPattern: Pattern
    ) {

        _playbackPattern.value =
            newPattern

        scope.launch {

            playerPreferences.savePlaybackPattern(
                newPattern
            )
        }

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
            queue.currentSegment()
                ?: return

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
            queue.nextSegment()
                ?: return

        updateState()

        playSegment(next)
    }

    fun prev() {

        val prev =
            queue.prevSegment()
                ?: return

        updateState()

        playSegment(prev)
    }

    fun playFrom(
        key: SegmentKey
    ) {

        val found =
            queue.findByKey(key)
                ?: return

        _isPlaying.value = true

        updateState()

        playSegment(found)
    }

    private fun playSegment(
        segment: Segment
    ) {

        segmentPlayer.stop()

        updateState()

        val currentTrack =
            queue.currentTrack()
                ?: return

        val steps =
            sequenceBuilder.build(
                track = currentTrack.track,
                segment = segment,
                pattern = _playbackPattern.value
            )

        segmentPlayer.play(steps) {

            if (!_isPlaying.value) {
                return@play
            }

            if (_pinnedKey.value != null) {

                val pinned =
                    queue.findByKey(
                        _pinnedKey.value!!
                    )

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

        _currentTrack.value =
            track

        _currentSegment.value =
            segment

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
    // Flag
    // ============================================================

    fun toggleFlag() {

        val current =
            _currentKey.value
                ?: return

        queue.updateSegment(current) {

            it.copy(
                flagged = !it.flagged
            )

        }.also {

            updateState()

            saveFlags(
                queue.allTracks()
            )
        }
    }

    // ============================================================
    // Pin
    // ============================================================

    fun togglePin() {

        val current =
            _currentKey.value
                ?: return

        _pinnedKey.value =
            if (_pinnedKey.value == current) {

                null

            } else {

                current
            }
    }

    fun isPinned(): Boolean {

        val current =
            _currentKey.value
                ?: return false

        return _pinnedKey.value == current
    }
}