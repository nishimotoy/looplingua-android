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

    fun play(
        steps: List<PlaybackStep>,
        onComplete: () -> Unit
    ) {
        handler.removeCallbacksAndMessages(null) // ★追加
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
        Log.d("PLAYER", "Step $index ${step.stepType} start=${step.startMs} end=${step.endMs} pause=${step.pauseMs}")

        when (step.stepType) {

            StepType.ORIGINAL,
            StepType.TRANSLATION,
            StepType.MEMO -> {

                val path = step.audioPath
                val start = step.startMs
                val end = step.endMs

                if (path == null || start == null || end == null) {
                    Log.w("PLAYER", "Skipping step with null audio: $step")
                    playStep(steps, index + 1, onComplete)
                    return
                }

                audioPlayer.play(path, start, end) {
                    if (!isStopped) playStep(steps, index + 1, onComplete)
                }
            }

            StepType.PAUSE_SHORT,
            StepType.PAUSE_LONG -> {
                val pauseDuration = step.pauseMs.takeIf { it > 0 } ?: if (step.stepType == StepType.PAUSE_SHORT) 500 else 1500

                // Pause を currentPosition 監視で扱う場合、AudioPlayer に playPause() を追加するとより安全
                handler.postDelayed({
                    if (!isStopped) playStep(steps, index + 1, onComplete)
                }, pauseDuration)
            }
        }
    }

    fun stop() {
        isStopped = true
        audioPlayer.stop()
        handler.removeCallbacksAndMessages(null)
    }
}