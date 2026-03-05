package com.looplingua.app.service.playback

import android.content.Context
import android.os.Handler
import android.os.Looper

class SimpleSegmentPlayer(private val context: Context) {

    private val handler = Handler(Looper.getMainLooper())
    private var steps: List<PlayerStep> = emptyList()
    private var currentStepIndex = 0

    fun playSteps(stepList: List<PlayerStep>) {
        steps = stepList
        currentStepIndex = 0
        executeNextStep()
    }

    private fun executeNextStep() {
        if (currentStepIndex >= steps.size) return

        val step = steps[currentStepIndex]
        when (step) {
            is PlayerStep.PlayOriginal -> {
                playOriginal(step.startMs, step.endMs)
                next()
            }
            is PlayerStep.PlayTranslation -> {
                playTranslation(step.startMs, step.endMs)
                next()
            }
            is PlayerStep.PlayMemo -> {
                playMemo(step.startMs, step.endMs)
                next()
            }
            is PlayerStep.Pause -> {
                pause(step.durationMs)
            }
        }
    }

    private fun next() {
        currentStepIndex++
        executeNextStep()
    }

    private fun playOriginal(startMs: Long, endMs: Long) {
        println("Play ORIGINAL $startMs - $endMs")
    }

    private fun playTranslation(startMs: Long, endMs: Long) {
        println("Play TRANSLATION $startMs - $endMs")
    }

    private fun playMemo(startMs: Long, endMs: Long) {
        println("Play MEMO $startMs - $endMs")
    }

    private fun pause(durationMs: Long) {
        handler.postDelayed({
            next()
        }, durationMs)
    }
}