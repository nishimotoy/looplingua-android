package com.looplingua.app.domain.playback

object PatternRepository {

    fun get(pattern: Pattern): PatternDefinition {
        return when (pattern) {

            Pattern.BASIC -> PatternDefinition(
                name = "BASIC",
                steps = listOf(
                    StepType.TRANSLATION,
                    StepType.PAUSE_SHORT,
                    StepType.ORIGINAL,
                    StepType.PAUSE_SHORT,
                    StepType.ORIGINAL,
                    StepType.PAUSE_SHORT,
                    StepType.MEMO
                )
            )

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