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
import com.looplingua.app.ui.SegmentScreen
import com.looplingua.app.ui.track.TrackScreen
import androidx.compose.runtime.collectAsState

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

        val segments = listOf(
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
            )
        )

        setContent {

            val currentSegment = controller.currentSegment.collectAsState().value

            val currentIndex = segments.indexOfFirst {
                it.id == currentSegment?.id
            }

            TrackScreen(
                segments = segments,
                currentIndex = currentIndex,
                onSegmentClick = { index ->
                    controller.playFrom(index)
                }
            )
        }

        controller.setSegments(segments)
        controller.play()
    }
}