package com.looplingua.app.domain.playback

import android.content.Context
import com.looplingua.app.data.pattern.PatternLoader

object PatternRepository {

    fun get(context: Context, pattern: Pattern): PatternDefinition {
        return when (pattern) {

            Pattern.BASIC -> PatternLoader.load(context, "patterns/basic.json")

            Pattern.SHADOWING -> PatternDefinition(
                name = "SHADOWING",
                steps = listOf(
                    StepType.TRANSLATION,
                    StepType.PAUSE_SHORT,
                    StepType.ORIGINAL,
                    StepType.PAUSE_LONG,
                    StepType.ORIGINAL,
                    StepType.PAUSE_LONG
                )
            )

            Pattern.ORIGINAL_ONLY -> PatternDefinition(
                name = "ORIGINAL_ONLY",
                steps = listOf(
                    StepType.ORIGINAL
                )
            )
        }
    }
}