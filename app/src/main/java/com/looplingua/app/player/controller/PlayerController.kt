package com.looplingua.app.player.controller

import com.looplingua.app.data.PlayerPreferences
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.SegmentKey
import com.looplingua.app.domain.model.TrackWithSegments
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.audio.AudioPlayerManager
import com.looplingua.app.player.queue.TrackQueue
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.player.segment.SegmentPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerController(
    private val queue: TrackQueue,
    private val sequenceBuilder: SequenceBuilder,
    private val segmentPlayer: SegmentPlayer,
    private val saveFlags: (List<TrackWithSegments>) -> Unit,
    private val playerPreferences: PlayerPreferences
) {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )

    // ============================================================
    // Project
    // ============================================================

    private var projectId: String? = null

    fun setProjectId(projectId: String) {
        this.projectId = projectId
    }

    suspend fun getLastProjectId(): String? {
        return playerPreferences.getLastProjectId()
    }

    // ============================================================
    // Playback Position    起動時の復元完了を判別するフラグ
    // ============================================================

    private var playbackPositionRestored = false

    // ============================================================
    // Pattern
    // ============================================================

    private val _playbackPattern = MutableStateFlow(Pattern.BASIC)

    val playbackPattern: StateFlow<Pattern> =
        _playbackPattern.asStateFlow()

    // ============================================================
    // Pause Multiplier
    // ============================================================

    private val _shortPauseMultiplier =
        MutableStateFlow<Float?>(null)

    val shortPauseMultiplier: StateFlow<Float?> =
        _shortPauseMultiplier.asStateFlow()

    private val _longPauseMultiplier =
        MutableStateFlow<Float?>(null)

    val longPauseMultiplier: StateFlow<Float?> =
        _longPauseMultiplier.asStateFlow()

    init {
        scope.launch {
            playerPreferences.playbackPattern.collect { pattern ->
                _playbackPattern.value = pattern
            }
        }

        scope.launch {
            playerPreferences.playbackSpeed.collect { speed ->
                _playbackSpeed.value = speed
                segmentPlayer.setPlaybackSpeed(speed ?: 1.0f)
            }
        }

        scope.launch {
            playerPreferences.shortPauseMultiplier.collect { multiplier ->
                _shortPauseMultiplier.value = multiplier
            }
        }

        scope.launch {
            playerPreferences.longPauseMultiplier.collect { multiplier ->
                _longPauseMultiplier.value = multiplier
            }
        }
    }

    fun setPattern(newPattern: Pattern) {
        _playbackPattern.value = newPattern

        _playbackSpeed.value = null

        _shortPauseMultiplier.value = null
        _longPauseMultiplier.value = null

        scope.launch {
            playerPreferences.savePlaybackPattern(newPattern)
            playerPreferences.clearPlaybackSpeed()
            playerPreferences.clearShortPauseMultiplier()
            playerPreferences.clearLongPauseMultiplier()
        }

        if (_isPlaying.value) {
            stop()
            play()
        }
    }

    // ============================================================
    // Playback Speed
    // ============================================================

    private val _playbackSpeed =
        MutableStateFlow<Float?>(null)

    val playbackSpeed: StateFlow<Float?> =
        _playbackSpeed.asStateFlow()

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        segmentPlayer.setPlaybackSpeed(speed)

        scope.launch {
            playerPreferences.savePlaybackSpeed(speed)
        }
    }

    // ============================================================
    // Current Segment
    // ============================================================

    private val _currentSegment = MutableStateFlow<Segment?>(null)

    val currentSegment: StateFlow<Segment?> =
        _currentSegment.asStateFlow()

    // ============================================================
    // Current Track
    // ============================================================

    private val _currentTrack = MutableStateFlow<TrackWithSegments?>(null)

    // ============================================================
    // Tracks
    // ============================================================

    private val _tracks =
        MutableStateFlow<List<TrackWithSegments>>(emptyList())

    val tracks: StateFlow<List<TrackWithSegments>> =
        _tracks.asStateFlow()

    // ============================================================
    // Playing State
    // ============================================================

    private val _isPlaying = MutableStateFlow(false)

    val isPlaying: StateFlow<Boolean> =
        _isPlaying.asStateFlow()

    // ============================================================
    // Current Key
    // ============================================================

    private val _currentKey = MutableStateFlow<SegmentKey?>(null)

    val currentKey: StateFlow<SegmentKey?> =
        _currentKey.asStateFlow()

    // ============================================================
    // Pin
    // ============================================================

    private val _pinnedKey = MutableStateFlow<SegmentKey?>(null)

    val pinnedKey: StateFlow<SegmentKey?> =
        _pinnedKey.asStateFlow()

    // ============================================================
    // Tracks from saved position
    // ============================================================

    fun setTracks(tracks: List<TrackWithSegments>) {
        playbackPositionRestored = false

        queue.setTracks(tracks)
        _tracks.value = queue.allTracks()
        updateState()

        scope.launch {
            val savedPosition =
                playerPreferences.playbackPosition.first()

            if (
                savedPosition != null &&
                savedPosition.projectId == projectId
            ) {
                queue.findByKey(
                    SegmentKey(
                        trackId = savedPosition.trackId,
                        segmentId = savedPosition.segmentId
                    )
                )
            }

            updateState()

            playbackPositionRestored = true

            if (_isPlaying.value) {
                playCurrentSegment()
            }
        }
    }

    // ============================================================
    // Tracks without saved position
    // ============================================================

    fun setTracksAndPlayFrom(
        tracks: List<TrackWithSegments>,
        key: SegmentKey
    ) {
        playbackPositionRestored = true
        queue.setTracks(tracks)
        _tracks.value = queue.allTracks()
        queue.findByKey(key)
        updateState()
        _isPlaying.value = true
        playCurrentSegment()
    }

    // ============================================================
    // Playback
    // ============================================================

    fun play() {
        if (!playbackPositionRestored) {
            _isPlaying.value = true
            return
        }

        if (_isPlaying.value) return

        val segment = queue.currentSegment() ?: return

        _isPlaying.value = true
        playSegment(segment)
    }

    private fun playCurrentSegment() {
        if (!_isPlaying.value) return

        val segment = queue.currentSegment() ?: return

        playSegment(segment)
    }

    fun stop() {
        _isPlaying.value = false
        segmentPlayer.stop()
        AudioPlayerManager.stopAll()
    }

    fun togglePlay() {
        if (_isPlaying.value) {
            stop()
        } else {
            play()
        }
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

        val playbackSpeed =
            sequenceBuilder.resolvePlaybackSpeed(
                pattern = _playbackPattern.value,
                playbackSpeedOverride = _playbackSpeed.value
            )

        segmentPlayer.setPlaybackSpeed(playbackSpeed)

        val steps = sequenceBuilder.build(
            track = currentTrack.track,
            segment = segment,
            pattern = _playbackPattern.value,
            shortPauseMultiplierOverride =
                _shortPauseMultiplier.value,
            longPauseMultiplierOverride =
                _longPauseMultiplier.value
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
                val next = queue.nextPlayableSegment()

                if (next != null) {
                    playSegment(next)
                } else {
                    val restart = queue.rewindToStart()

                    if (restart != null && !restart.skipped) {
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
        val track = queue.currentTrack()
        val segment = queue.currentSegment()

        _currentTrack.value = track
        _currentSegment.value = segment

        val newKey =
            if (track != null && segment != null) {
                SegmentKey(
                    trackId = track.track.id,
                    segmentId = segment.id
                )
            } else {
                null
            }

        val keyChanged = _currentKey.value != newKey
        _currentKey.value = newKey

        /*
         * 起動時の復元前には保存しない。
         *
         * これにより、setTracks()直後に一旦セットされる
         * 先頭Segmentで保存位置を上書きすることを防ぐ。
         *
         * 実際の再生中にSegmentが変わった場合だけ、
         * 最新のProject + Track + Segmentを
         * DataStoreへ保存する。
         */
        if (
            playbackPositionRestored &&
            keyChanged &&
            newKey != null
        ) {
            savePlaybackPosition(newKey)
        }
    }

    private fun savePlaybackPosition(key: SegmentKey) {
        val currentProjectId = projectId ?: return

        scope.launch {
            playerPreferences.savePlaybackPosition(
                projectId = currentProjectId,
                trackId = key.trackId,
                segmentId = key.segmentId
            )
        }
    }

    // ============================================================
    // Flag
    // ============================================================

    fun toggleFlag() {
        val current = _currentKey.value ?: return

        queue.updateSegment(current) {
            it.copy(flagged = !it.flagged)
        }.also {
            _tracks.value = queue.allTracks()
            updateState()
            saveFlags(queue.allTracks())
        }
    }

    // ============================================================
    // Skip
    // ============================================================

    fun toggleSkip() {
        val current = _currentKey.value ?: return

        queue.updateSegment(current) {
            it.copy(skipped = !it.skipped)
        }.also {
            _tracks.value = queue.allTracks()
            updateState()
            saveFlags(queue.allTracks())
        }
    }

    // ============================================================
    // Pin
    // ============================================================

    fun togglePin() {
        val current = _currentKey.value ?: return

        _pinnedKey.value =
            if (_pinnedKey.value == current) {
                null
            } else {
                current
            }
    }

    fun isPinned(): Boolean {
        val current = _currentKey.value ?: return false

        return _pinnedKey.value == current
    }

    // ============================================================
    // Pause Multiplier
    // ============================================================

    fun setShortPauseMultiplier(
        multiplier: Float
    ) {
        _shortPauseMultiplier.value =
            multiplier

        scope.launch {
            playerPreferences.saveShortPauseMultiplier(
                multiplier
            )
        }
    }

    fun setLongPauseMultiplier(
        multiplier: Float
    ) {
        _longPauseMultiplier.value =
            multiplier

        scope.launch {
            playerPreferences.saveLongPauseMultiplier(
                multiplier
            )
        }
    }

    // ============================================================
    // Release
    // ============================================================

    fun release() {
        _isPlaying.value = false
        segmentPlayer.release()
        scope.cancel()
    }
}