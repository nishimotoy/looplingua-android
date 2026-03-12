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

    fun play(
        steps: List<PlaybackStep>,
        onComplete: () -> Unit
    ) {
        playStep(steps, 0, onComplete)
    }

    private fun playStep(
        steps: List<PlaybackStep>,
        index: Int,
        onComplete: () -> Unit
    ) {

        if (index >= steps.size) {
            onComplete()
            return
        }

        val step = steps[index]
        Log.d("PLAYER", "Step $index ${step.stepType}")

        when (step.stepType) {

            StepType.ORIGINAL,
            StepType.TRANSLATION,
            StepType.MEMO -> {

                audioPlayer.play(
                    resId = step.audioResId!!,
                    startMs = step.startMs!!,
                    endMs = step.endMs!!
                ) {
                    // 再生が終わったら次のステップ
                    playStep(steps, index + 1, onComplete)
                }
            }

            StepType.PAUSE_SHORT -> {
                handler.postDelayed({
                    playStep(steps, index + 1, onComplete)
                }, 800)
            }

            StepType.PAUSE_LONG -> {
                handler.postDelayed({
                    playStep(steps, index + 1, onComplete)
                }, 1500)
            }
        }
    }

    fun stop() {
        audioPlayer.stop()
        handler.removeCallbacksAndMessages(null)
    }
}