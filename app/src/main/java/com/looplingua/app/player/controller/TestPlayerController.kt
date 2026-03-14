package com.looplingua.app.player.controller

import android.util.Log
import com.looplingua.app.R
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.player.audio.AudioPlayer
import com.looplingua.app.player.segment.SegmentPlayer
import com.looplingua.app.domain.playback.StepType
import com.looplingua.app.domain.playback.PlaybackStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TestPlayerController(
    private val audioPlayer: AudioPlayer
) {

    // UIに通知するStateFlow
    private val _playerState = MutableStateFlow<PlayerState?>(null)
    val playerState: StateFlow<PlayerState?> = _playerState

    private val segmentPlayer = SegmentPlayer(audioPlayer)

    private var segments: List<Segment> = emptyList()
    private var currentIndex = 0

    fun setSegments(list: List<Segment>) {
        segments = list
        currentIndex = 0
    }

    fun play() {
        if (segments.isEmpty()) return
        playSegment(segments[currentIndex])
    }

    fun stop() {
        segmentPlayer.stop()
        _playerState.value = null
    }

    private fun playSegment(segment: Segment) {

        val steps = mutableListOf<PlaybackStep>()

        // TRANSLATION
        if (segment.translationStartMs != null && segment.translationEndMs != null) {
            steps.add(
                PlaybackStep(
                    stepType = StepType.TRANSLATION,
                    audioResId = R.raw.greetings_en,
                    startMs = segment.translationStartMs,
                    endMs = segment.translationEndMs,
                    pauseMs = 0
                )
            )
        }

        // PAUSE_SHORT
        steps.add(
            PlaybackStep(
                stepType = StepType.PAUSE_SHORT,
                audioResId = null,
                startMs = null,
                endMs = null,
                pauseMs = 500
            )
        )

        // ORIGINAL ×2
        repeat(2) {
            steps.add(
                PlaybackStep(
                    stepType = StepType.ORIGINAL,
                    audioResId = R.raw.greetings_uk,
                    startMs = segment.originalStartMs,
                    endMs = segment.originalEndMs,
                    pauseMs = 0
                )
            )
            steps.add(
                PlaybackStep(
                    stepType = StepType.PAUSE_SHORT,
                    audioResId = null,
                    startMs = null,
                    endMs = null,
                    pauseMs = 500
                )
            )
        }

        // MEMO
        if (segment.memoStartMs != null && segment.memoEndMs != null) {
            steps.add(
                PlaybackStep(
                    stepType = StepType.MEMO,
                    audioResId = R.raw.greetings_memo,
                    startMs = segment.memoStartMs,
                    endMs = segment.memoEndMs,
                    pauseMs = 0
                )
            )
        }

        // UI通知
        _playerState.value =
            PlayerState(segment, steps.firstOrNull()?.stepType, true)

        // segmentPlayerにセットして再生
        segmentPlayer.play(steps) {
            Log.d("TEST_PLAYER", "Segment finished")
        }

        Log.d("TEST_PLAYER", "Segment started: ${segment.originalText}")
        // Stepごとのログを追加
        steps.forEach {
            Log.d("STEP_DEBUG", "Step: ${it.stepType}")
        }
    }
}

// PlayerState
data class PlayerState(
    val segment: Segment?,
    val stepType: StepType?,
    val isPlaying: Boolean
)