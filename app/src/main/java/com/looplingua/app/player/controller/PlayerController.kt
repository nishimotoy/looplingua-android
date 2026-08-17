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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerController(
    private val queue: TrackQueue,
    private val sequenceBuilder: SequenceBuilder,
    private val segmentPlayer: SegmentPlayer,
    private val saveFlags: (List<TrackWithSegments>) -> Unit,
    private val playerPreferences: PlayerPreferences
) {

    private val scope =
        CoroutineScope(
            SupervisorJob() +
                    Dispatchers.Main.immediate
        )

    // ============================================================
    // Playback Position
    // ============================================================

    /*
     * 起動時に保存済みの再生位置を復元するまで、
     * PLAYを開始しない。
     *
     * DataStoreは非同期なので、setTracks()直後に
     * MainActivityからplay()が呼ばれても、
     * 保存位置の復元が終わるまで待つ。
     */
    private var playbackPositionRestored = false

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

                _playbackPattern.value =
                    pattern
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

//  val currentTrack: StateFlow<TrackWithSegments?> =
//      _currentTrack.asStateFlow()

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

        playbackPositionRestored = false

        queue.setTracks(tracks)

        /*
         * まず現在状態をUIへ反映する。
         *
         * この時点ではまだ保存位置の復元前なので、
         * updateState()から再生位置を保存することはない。
         */
        updateState()

        /*
         * 保存済みのTrack + Segmentを復元する。
         */
        scope.launch {

            val savedPosition =
                playerPreferences.playbackPosition.first()

            if (savedPosition != null) {

                queue.findByKey(
                    SegmentKey(
                        trackId = savedPosition.trackId,
                        segmentId = savedPosition.segmentId
                    )
                )
            }

            /*
             * 保存位置が見つからなかった場合は、
             * TrackQueueがセットした先頭位置のままにする。
             */
            updateState()

            playbackPositionRestored = true

            /*
             * MainActivityなどから復元前にplay()が呼ばれていた
             * 場合は、ここで再生を開始する。
             */
            if (_isPlaying.value) {
                playCurrentSegment()
            }
        }
    }

    // ============================================================
    // Playback
    // ============================================================

    fun play() {

        if (!playbackPositionRestored) {

            /*
             * setTracks()で復元処理が完了した後、
             * _isPlayingをtrueにしていれば再生される。
             */
            _isPlaying.value = true
            return
        }

        if (_isPlaying.value) return

        val segment =
            queue.currentSegment()
                ?: return

        _isPlaying.value = true

        playSegment(segment)
    }

    private fun playCurrentSegment() {

        if (!_isPlaying.value) return

        val segment =
            queue.currentSegment()
                ?: return

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

        val newKey =
            if (track != null && segment != null) {

                SegmentKey(
                    trackId = track.track.id,
                    segmentId = segment.id
                )

            } else {

                null
            }

        val keyChanged =
            _currentKey.value != newKey

        _currentKey.value =
            newKey

        /*
         * 起動時の復元前には保存しない。
         *
         * これにより、setTracks()直後に一旦セットされる
         * 先頭Segmentで保存位置を上書きすることを防ぐ。
         *
         * 実際の再生中にSegmentが変わった場合だけ、
         * 最新のTrack + SegmentをDataStoreへ保存する。
         */
        if (
            playbackPositionRestored &&
            keyChanged &&
            newKey != null
        ) {

            savePlaybackPosition(
                newKey
            )
        }
    }

    private fun savePlaybackPosition(
        key: SegmentKey
    ) {

        scope.launch {

            playerPreferences.savePlaybackPosition(
                trackId = key.trackId,
                segmentId = key.segmentId
            )
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