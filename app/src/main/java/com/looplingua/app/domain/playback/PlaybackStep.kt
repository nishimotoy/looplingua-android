package com.looplingua.app.domain.playback

import com.looplingua.app.domain.model.Segment

sealed class PlaybackStep {

    data class PlayOriginal(
        val segment: Segment,
        val repeatIndex: Int
    ) : PlaybackStep()

    data class PlayTranslation(
        val segment: Segment
    ) : PlaybackStep()

    data class Pause(
        val durationMs: Long
    ) : PlaybackStep()
}