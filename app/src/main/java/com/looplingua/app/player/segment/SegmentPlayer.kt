package com.looplingua.app.player.segment

import com.looplingua.app.domain.playback.PlaybackStep
import com.looplingua.app.domain.playback.StepType
import com.looplingua.app.player.audio.AudioPlayer

class SegmentPlayer(
    private val audioPlayer: AudioPlayer
) {

    fun play(step: PlaybackStep) {

        when (step.stepType) {

            StepType.TRANSLATION,
            StepType.ORIGINAL,
            StepType.MEMO -> {

                val resId = step.audioResId ?: return
                val start = step.startMs ?: return
                val end = step.endMs ?: return

                audioPlayer.play(
                    resId = resId,
                    startMs = start,
                    endMs = end
                )
            }

            StepType.PAUSE_SHORT,
            StepType.PAUSE_LONG -> {
                // pauseはSegmentQueueが処理
            }
        }
    }

    fun stop() {
        audioPlayer.stop()
    }
}