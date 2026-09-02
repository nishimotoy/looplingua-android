package com.looplingua.app.player.segment

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.looplingua.app.domain.playback.PlaybackStep
import com.looplingua.app.domain.playback.StepType
import com.looplingua.app.player.audio.AudioPlayer

class SegmentPlayer(
    private val audioPlayer: AudioPlayer
) {

    private val handler = Handler(Looper.getMainLooper())
    private var isStopped = false

    fun setPlaybackSpeed(speed: Float) {
        audioPlayer.setPlaybackSpeed(speed)
    }

    fun play(
        steps: List<PlaybackStep>,
        onComplete: () -> Unit
    ) {
        Log.d(
            "PLAYER_TRACE",
            "SegmentPlayer.play() steps=${steps.size}"
        )

        handler.removeCallbacksAndMessages(null)
        isStopped = false
        playStep(steps, 0, onComplete)
    }

    private fun playStep(
        steps: List<PlaybackStep>,
        index: Int,
        onComplete: () -> Unit
    ) {
        if (isStopped) return

        if (index >= steps.size) {
            onComplete()
            return
        }

        val step = steps[index]

        Log.d(
            "PLAYER_STEP",
            "Step $index type=${step.stepType} pause=${step.pauseMs}"
        )

        when (step.stepType) {
            StepType.ORIGINAL,
            StepType.TRANSLATION,
            StepType.MEMO -> {
                val slice = step.slice

                if (slice == null) {
                    Log.w(
                        "PLAYER_STEP",
                        "Skipping step with null slice: $step"
                    )
                    playStep(steps, index + 1, onComplete)
                    return
                }

                audioPlayer.play(
                    slice.audioPath,
                    slice.startMs,
                    slice.endMs
                ) {
                    if (!isStopped) {
                        playStep(steps, index + 1, onComplete)
                    }
                }
            }

            StepType.PAUSE_SHORT,
            StepType.PAUSE_LONG -> {
                handler.postDelayed(
                    {
                        if (!isStopped) {
                            playStep(steps, index + 1, onComplete)
                        }
                    },
                    step.pauseMs
                )
            }
        }
    }

    fun stop() {
        isStopped = true
        audioPlayer.stop()
        handler.removeCallbacksAndMessages(null)
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        isStopped = true
        audioPlayer.release()
    }
}