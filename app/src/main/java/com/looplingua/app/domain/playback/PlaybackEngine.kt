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

            steps.add(PlaybackStep.PlayTranslation(segment))
            steps.add(PlaybackStep.Pause(config.shortPauseMs))
            steps.add(PlaybackStep.PlayOriginal(segment, 0))
            steps.add(PlaybackStep.Pause(shadowPause))
            steps.add(PlaybackStep.PlayOriginal(segment, 1))
        }

        return steps
    }
}