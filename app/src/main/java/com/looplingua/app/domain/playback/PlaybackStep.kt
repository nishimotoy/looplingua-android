package com.looplingua.app.domain.playback

import com.looplingua.app.domain.model.Segment

sealed class PlaybackStep {

    /**
     * Play original audio segment
     */
    data class PlayOriginal(
        val segment: Segment,
        val repeatIndex: Int = 0
    ) : PlaybackStep()

    /**
     * Play translation audio (TTS or pre-generated audio)
     */
    data class PlayTranslation(
        val segment: Segment
    ) : PlaybackStep()

    /**
     * Silent pause
     */
    data class Pause(
        val durationMs: Long
    ) : PlaybackStep()
}
