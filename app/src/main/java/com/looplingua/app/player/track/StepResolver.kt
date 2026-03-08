package com.looplingua.app.player.track

import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.playback.AudioSource
import com.looplingua.app.domain.playback.PlaybackStep
import com.looplingua.app.domain.playback.StepType

class StepResolver {

    fun buildSteps(track: Track): List<PlaybackStep> {

        val steps = mutableListOf<PlaybackStep>()

        for (segment in track.segments) {

            // ORIGINAL
            steps.add(
                PlaybackStep(
                    segmentId = segment.id,
                    startMs = segment.startMs,
                    endMs = segment.endMs,
                    stepType = StepType.ORIGINAL,
                    audioSource = AudioSource.ORIGINAL
                )
            )

            // TRANSLATION
            steps.add(
                PlaybackStep(
                    segmentId = segment.id,
                    startMs = segment.startMs,
                    endMs = segment.endMs,
                    stepType = StepType.TRANSLATION,
                    audioSource = AudioSource.TRANSLATION
                )
            )

            // ORIGINAL repeat
            steps.add(
                PlaybackStep(
                    segmentId = segment.id,
                    startMs = segment.startMs,
                    endMs = segment.endMs,
                    stepType = StepType.ORIGINAL,
                    audioSource = AudioSource.ORIGINAL
                )
            )

            // SILENCE (500ms)
            steps.add(
                PlaybackStep(
                    segmentId = segment.id,
                    startMs = segment.endMs,
                    endMs = segment.endMs + 500,
                    stepType = StepType.SILENCE,
                    audioSource = AudioSource.ORIGINAL
                )
            )
        }

        return steps
    }
}