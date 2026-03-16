package com.looplingua.app.player.sequence

import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.domain.playback.PlaybackStep
import com.looplingua.app.domain.playback.StepType
import com.looplingua.app.domain.model.AudioSlice

class SequenceBuilder {

    fun build(
        track: Track,
        segment: Segment,
        pattern: Pattern
    ): List<PlaybackStep> {

        val steps = mutableListOf<PlaybackStep>()

        for (type in pattern.steps()) {

            when (type) {

                StepType.TRANSLATION -> {
                    if (hasAudio(segment.translationStartMs, segment.translationEndMs)) {

                        val slice = AudioSlice(
                            audioPath = track.translationAudioPath!!,
                            startMs = segment.translationStartMs,
                            endMs = segment.translationEndMs
                        )

                        steps.add(
                            PlaybackStep(
                                stepType = StepType.TRANSLATION,
                                slice = slice,
                                pauseMs = 0
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
                            pauseMs = 0
                        )
                    )
                }

                StepType.MEMO -> {
                    if (hasAudio(segment.memoStartMs, segment.memoEndMs)) {

                        val slice = AudioSlice(
                            audioPath = track.memoAudioPath!!,
                            startMs = segment.memoStartMs,
                            endMs = segment.memoEndMs
                        )

                        steps.add(
                            PlaybackStep(
                                stepType = StepType.MEMO,
                                slice = slice,
                                pauseMs = 0
                            )
                        )
                    }
                }

                StepType.PAUSE_SHORT -> {
                    steps.add(
                        PlaybackStep(
                            stepType = StepType.PAUSE_SHORT,
                            slice = null,
                            pauseMs = 400
                        )
                    )
                }

                StepType.PAUSE_LONG -> {
                    steps.add(
                        PlaybackStep(
                            stepType = StepType.PAUSE_LONG,
                            slice = null,
                            pauseMs = 1000
                        )
                    )
                }
            }
        }

        return steps
    }

    private fun hasAudio(start: Long?, end: Long?): Boolean {
        if (start == null || end == null) return false
        return end > start
    }
}