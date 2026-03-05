package com.looplingua.app.service.playback

import com.looplingua.app.domain.model.Track
import com.looplingua.app.service.playback.PlayerStep

class StepResolver {

    // PlaybackPattern / PlaybackConfig は null で無視
    fun buildSteps(track: Track, pattern: Any?, config: Any?): List<PlayerStep> {
        val steps = mutableListOf<PlayerStep>()

        track.segments.forEach { segment ->

            // オリジナルは必ず再生
            steps.add(PlayerStep.PlayOriginal(segment.original.startMs, segment.original.endMs))

            // 翻訳・メモはあれば再生
            segment.translation?.let { steps.add(PlayerStep.PlayTranslation(it.startMs, it.endMs)) }
            segment.memo?.let { steps.add(PlayerStep.PlayMemo(it.startMs, it.endMs)) }

            // 短ポーズ
            steps.add(PlayerStep.Pause(500L))
        }

        return steps
    }
}