package com.looplingua.app.domain.playback

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track

class PlaybackEngine {

    fun buildPlaybackSteps(
        track: Track,
        segments: List<Segment>,
        config: PlaybackConfig
    ): List<PlaybackStep> {

        val steps = mutableListOf<PlaybackStep>()

        val orderedSegments = track.segmentIds.mapNotNull { id ->
            segments.find { it.id == id }
        }

        for (segment in orderedSegments) {

            val segmentDuration =
                segment.endTimeMs - segment.startTimeMs

            val shadowPause =
                (segmentDuration * config.shadowingPauseMultiplier).toLong()

            // 1. Translation
            steps.add(
                PlaybackStep.PlayTranslation(segment)
            )

            // 2. Short boundary pause
            steps.add(
                PlaybackStep.Pause(config.shortPauseMs)
            )

            // 3. Original (first)
            steps.add(
                PlaybackStep.PlayOriginal(
                    segment = segment,
                    repeatIndex = 0
                )
            )

            // 4. Shadowing pause
            steps.add(
                PlaybackStep.Pause(shadowPause)
            )

            // 5. Original (second)
            steps.add(
                PlaybackStep.PlayOriginal(
                    segment = segment,
                    repeatIndex = 1
                )
            )
        }

        return steps
    }
}
