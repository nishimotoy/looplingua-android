package com.looplingua.app

import com.looplingua.app.domain.model.AudioSlice
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track

object SampleTracks {

    fun greetings(): Track {

        val s1 = Segment(
            id = "1",
            original = AudioSlice(0, 2000),
            translation = AudioSlice(0, 1500),
            memo = null,
            originalText = "Добрий день",
            translationText = "Hello",
            memoText = null
        )

        val s2 = Segment(
            id = "2",
            original = AudioSlice(2000,4000),
            translation = AudioSlice(1500,3000),
            memo = null,
            originalText = "Доброго ранку",
            translationText = "Good morning",
            memoText = null
        )

        return Track(
            originalAudioPath =
                "android.resource://com.looplingua.app/raw/greetings_uk",

            translationAudioPath =
                "android.resource://com.looplingua.app/raw/greetings_en",

            memoAudioPath =
                "android.resource://com.looplingua.app/raw/greetings_memo",

            segments = listOf(s1,s2)
        )
    }
}