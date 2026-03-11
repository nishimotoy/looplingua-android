package com.looplingua.app.domain.playback

enum class Pattern {

    BASIC,
    SHADOWING,
    ORIGINAL_ONLY;

    fun steps(): List<StepType> {

        return when (this) {

            BASIC -> listOf(
                StepType.TRANSLATION,
                StepType.PAUSE_SHORT,
                StepType.ORIGINAL,
                StepType.PAUSE_SHORT,
                StepType.ORIGINAL,
                StepType.PAUSE_SHORT,
                StepType.MEMO
            )

            SHADOWING -> listOf(
                StepType.TRANSLATION,
                StepType.PAUSE_SHORT,
                StepType.ORIGINAL,
                StepType.PAUSE_LONG,
                StepType.ORIGINAL,
                StepType.PAUSE_LONG
            )

            ORIGINAL_ONLY -> listOf(
                StepType.ORIGINAL
            )
        }
    }
}