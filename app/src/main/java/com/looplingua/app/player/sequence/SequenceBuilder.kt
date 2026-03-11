package com.looplingua.app.player.sequence

import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.domain.playback.PlaybackStep
import com.looplingua.app.domain.playback.StepType

class SequenceBuilder {

    private val PAUSE_SHORT = 400L
    private val PAUSE_LONG = 1200L

    fun build(
        track: Track,
        segment: Segment,
        pattern: Pattern
    ): List<PlaybackStep> {

        val steps = mutableListOf<PlaybackStep>()

        for (type in pattern.steps()) {

            when (type) {

                StepType.TRANSLATION -> {
                    if (segment.translationStartMs != null && segment.translationEndMs != null) {
                        steps.add(
                            PlaybackStep(
                                stepType = StepType.TRANSLATION,
                                audioResId = track.translationResId,
                                startMs = segment.translationStartMs,
                                endMs = segment.translationEndMs,
                                pauseMs = 0
                            )
                        )
                    }
                }

                StepType.ORIGINAL -> {
                    steps.add(
                        PlaybackStep(
                            stepType = StepType.ORIGINAL,
                            audioResId = track.originalResId,
                            startMs = segment.originalStartMs,
                            endMs = segment.originalEndMs,
                            pauseMs = 0
                        )
                    )
                }

                StepType.MEMO -> {
                    if (segment.memoStartMs != null && segment.memoEndMs != null) {
                        steps.add(
                            PlaybackStep(
                                stepType = StepType.MEMO,
                                audioResId = track.memoResId,
                                startMs = segment.memoStartMs,
                                endMs = segment.memoEndMs,
                                pauseMs = 0
                            )
                        )
                    }
                }

                StepType.PAUSE_SHORT -> {
                    steps.add(
                        PlaybackStep(
                            stepType = StepType.PAUSE_SHORT,
                            audioResId = null,
                            startMs = null,
                            endMs = null,
                            pauseMs = 800
                        )
                    )
                }

                StepType.PAUSE_LONG -> {
                    steps.add(
                        PlaybackStep(
                            stepType = StepType.PAUSE_LONG,
                            audioResId = null,
                            startMs = null,
                            endMs = null,
                            pauseMs = 2000
                        )
                    )
                }
            }
        }

        return steps
    }
}