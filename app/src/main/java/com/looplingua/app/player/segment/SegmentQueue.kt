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

    private var onFinished: (() -> Unit)? = null


    fun setSteps(stepList: List<PlaybackStep>) {

        steps = stepList
        currentIndex = 0
    }


    fun setOnFinishedListener(listener: () -> Unit) {

        onFinished = listener
    }


    fun start() {

        playNext()
    }


    private fun playNext() {

        if (currentIndex >= steps.size) {

            onFinished?.invoke()
            return
        }

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