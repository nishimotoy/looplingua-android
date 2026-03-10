package com.looplingua.app.player.segment

import android.os.Handler
import android.os.Looper
import com.looplingua.app.domain.playback.PlaybackStep
import com.looplingua.app.domain.playback.StepType

class SegmentQueue(
    private val segmentPlayer: SegmentPlayer
) {

    private var steps: List<PlaybackStep> = emptyList()

    private var currentIndex = 0

    private val handler = Handler(Looper.getMainLooper())

    fun setSteps(stepList: List<PlaybackStep>) {

        steps = stepList
        currentIndex = 0
    }

    fun start() {

        playNext()
    }

    private fun playNext() {

        if (currentIndex >= steps.size) return

        val step = steps[currentIndex]

        segmentPlayer.play(step)

        val delay = when (step.stepType) {

            StepType.PAUSE_SHORT,
            StepType.PAUSE_LONG -> step.pauseMs

            else -> (step.endMs!! - step.startMs!!)
        }

        currentIndex++

        handler.postDelayed({

            playNext()

        }, delay)
    }

    fun stop() {

        handler.removeCallbacksAndMessages(null)

        segmentPlayer.stop()

        currentIndex = 0
    }
}