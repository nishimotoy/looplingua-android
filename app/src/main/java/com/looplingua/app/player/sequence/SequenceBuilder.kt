package com.looplingua.app.player.sequence

import com.looplingua.app.domain.model.AudioSlice
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.domain.playback.PatternProvider
import com.looplingua.app.domain.playback.PlaybackStep
import com.looplingua.app.domain.playback.StepType

class SequenceBuilder(
    private val patternProvider: PatternProvider
) {

    private val shortPauseMs = 400L

    fun build(
        track: Track,
        segment: Segment,
        pattern: Pattern,
        shortPauseMultiplierOverride: Float? = null,
        longPauseMultiplierOverride: Float? = null
    ): List<PlaybackStep> {

        val steps = mutableListOf<PlaybackStep>()
        val definition = patternProvider.get(pattern)

        val originalDuration =
            segment.originalEndMs - segment.originalStartMs

        for (patternStep in definition.steps) {

            when (patternStep.type) {

                StepType.TRANSLATION -> {

                    val path = track.translationAudioPath

                    if (
                        path != null &&
                        hasAudio(
                            segment.translationStartMs,
                            segment.translationEndMs
                        )
                    ) {

                        val slice = AudioSlice(
                            audioPath = path,
                            startMs = segment.translationStartMs,
                            endMs = segment.translationEndMs
                        )

                        steps.add(
                            PlaybackStep(
                                stepType = StepType.TRANSLATION,
                                slice = slice,
                                pauseMs = 0,
                                pauseMultiplier = 1.0f
                            )
                        )
                    }
                }

                StepType.ORIGINAL -> {

                    val slice = AudioSlice(
                        audioPath = track.originalAudioPath,
                        startMs = segment.originalStartMs,
                        endMs = segment.originalEndMs
                    )

                    steps.add(
                        PlaybackStep(
                            stepType = StepType.ORIGINAL,
                            slice = slice,
                            pauseMs = 0,
                            pauseMultiplier = 1.0f
                        )
                    )
                }

                StepType.MEMO -> {

                    val path = track.memoAudioPath

                    if (
                        path != null &&
                        hasAudio(
                            segment.memoStartMs,
                            segment.memoEndMs
                        )
                    ) {

                        val slice = AudioSlice(
                            audioPath = path,
                            startMs = segment.memoStartMs,
                            endMs = segment.memoEndMs
                        )

                        steps.add(
                            PlaybackStep(
                                stepType = StepType.MEMO,
                                slice = slice,
                                pauseMs = 0,
                                pauseMultiplier = 1.0f
                            )
                        )
                    }
                }

                StepType.PAUSE_SHORT -> {

                    val pauseMultiplier =
                        shortPauseMultiplierOverride
                            ?: patternStep.multiplier

                    val pauseMs =
                        (shortPauseMs * pauseMultiplier).toLong()

                    steps.add(
                        PlaybackStep(
                            stepType = StepType.PAUSE_SHORT,
                            slice = null,
                            pauseMs = pauseMs,
                            pauseMultiplier = pauseMultiplier
                        )
                    )
                }

                StepType.PAUSE_LONG -> {

                    val pauseMultiplier =
                        longPauseMultiplierOverride
                            ?: patternStep.multiplier

                    val pauseMs =
                        (originalDuration * pauseMultiplier).toLong()

                    steps.add(
                        PlaybackStep(
                            stepType = StepType.PAUSE_LONG,
                            slice = null,
                            pauseMs = pauseMs,
                            pauseMultiplier = pauseMultiplier
                        )
                    )
                }
            }
        }
        return steps
    }

    fun resolvePlaybackSpeed(
        pattern: Pattern,
        playbackSpeedOverride: Float?
    ): Float {
        val definition = patternProvider.get(pattern)

        return playbackSpeedOverride
            ?: definition.playbackSpeed
    }

    private fun hasAudio(
        start: Long?,
        end: Long?
    ): Boolean {

        if (start == null || end == null) {
            return false
        }
        return end > start
    }
}