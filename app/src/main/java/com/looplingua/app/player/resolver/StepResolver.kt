package com.looplingua.app.player.resolver

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.playback.PlaybackStep
import com.looplingua.app.playback.StepType

class StepResolver {

    fun resolve(track: Track, segment: Segment): List<PlaybackStep> {

        val steps = mutableListOf<PlaybackStep>()

        segment.translation?.let {

            steps.add(
                PlaybackStep(
                    type = StepType.TRANSLATION,
                    text = it
                )
            )

            steps.add(
                PlaybackStep(
                    type = StepType.PAUSE,
                    pauseMs = 600
                )
            )
        }

        steps.add(
            PlaybackStep(
                type = StepType.ORIGINAL,
                audioRes = track.audioRes,
                startMs = segment.startMs,
                endMs = segment.endMs
            )
        )

        steps.add(
            PlaybackStep(
                type = StepType.PAUSE,
                pauseMs = 600
            )
        )

        return steps
    }
}