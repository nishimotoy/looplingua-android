package com.looplingua.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.player.audio.AudioPlayer
import com.looplingua.app.player.controller.PlayerController
import com.looplingua.app.player.segment.SegmentPlaylist
import com.looplingua.app.player.segment.SegmentPlayer
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.ui.MainScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audioPlayer = AudioPlayer(this)
        val segmentPlayer = SegmentPlayer(audioPlayer)
        val playlist = SegmentPlaylist()
        val sequenceBuilder = SequenceBuilder()

        val track = Track(
            id = 1L,
            originalResId = R.raw.greetings_uk,
            translationResId = R.raw.greetings_en,
            memoResId = R.raw.greetings_memo
        )

        val controller = PlayerController(
            track = track,
            playlist = playlist,
            sequenceBuilder = sequenceBuilder,
            segmentPlayer = segmentPlayer
        )

        val segments = createTestSegments()

        controller.setSegments(segments)

        setContent {
            MainScreen(
                controller = controller,
                segments = segments
            )
        }
    }

    private fun createTestSegments(): List<Segment> {

        return listOf(
            Segment(
                id = 1L,
                originalStartMs = 100,
                originalEndMs = 2000,
                translationStartMs = 100,
                translationEndMs = 2000,
                memoStartMs = 0,
                memoEndMs = 0,
                originalText = "Добрий день",
                translationText = "Hello",
                memoText = ""
            ),
            Segment(
                id = 2L,
                originalStartMs = 2000,
                originalEndMs = 5000,
                translationStartMs = 2000,
                translationEndMs = 5000,
                memoStartMs = 0,
                memoEndMs = 0,
                originalText = "Доброго ранку",
                translationText = "Good morning",
                memoText = ""
            ),
            Segment(
                id = 3L,
                originalStartMs = 5000,
                originalEndMs = 7000,
                translationStartMs = 5000,
                translationEndMs = 7000,
                memoStartMs = 0,
                memoEndMs = 0,
                originalText = "Добрий вечір",
                translationText = "Good evening",
                memoText = ""
            ),
            Segment(
                id = 4L,
                originalStartMs = 7000,
                originalEndMs = 10000,
                translationStartMs = 7000,
                translationEndMs = 10000,
                memoStartMs = 0,
                memoEndMs = 0,
                originalText = "До побачення",
                translationText = "Goodbye",
                memoText = ""
            ),
            Segment(
                id = 5L,
                originalStartMs = 10000,
                originalEndMs = 14000,
                translationStartMs = 10000,
                translationEndMs = 14000,
                memoStartMs = 0,
                memoEndMs = 0,
                originalText = "Доброї ночі",
                translationText = "Good night",
                memoText = ""
            ),
            // 仮セグメント
        Segment(6L, 100,2000,100,2000,0,0,"Добрий день","Hello",""),
        Segment(7L, 2000,5000,2000,5000,0,0,"Доброго ранку","Good morning",""),
        Segment(8L, 5000,7000,5000,7000,0,0,"Добрий вечір","Good evening",""),
        Segment(9L, 7000,10000,7000,10000,0,0,"До побачення","Goodbye",""),
        Segment(10L, 10000,14000,10000,14000,0,0,"Доброї ночі","Good night",""),

        Segment(11L, 100,2000,100,2000,0,0,"Добрий день","Hello",""),
        Segment(12L, 2000,5000,2000,5000,0,0,"Доброго ранку","Good morning",""),
        Segment(13L, 5000,7000,5000,7000,0,0,"Добрий вечір","Good evening",""),
        Segment(14L, 7000,10000,7000,10000,0,0,"До побачення","Goodbye",""),
        Segment(15L, 10000,14000,10000,14000,0,0,"Доброї ночі","Good night",""),

        Segment(16L, 100,2000,100,2000,0,0,"Добрий день","Hello",""),
        Segment(17L, 2000,5000,2000,5000,0,0,"Доброго ранку","Good morning",""),
        Segment(18L, 5000,7000,5000,7000,0,0,"Добрий вечір","Good evening",""),
        Segment(19L, 7000,10000,7000,10000,0,0,"До побачення","Goodbye",""),
        Segment(20L, 10000,14000,10000,14000,0,0,"Доброї ночі","Good night","")
        )
    }
}