package com.looplingua.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.model.Segment

import com.looplingua.app.player.audio.AudioPlayer
import com.looplingua.app.player.segment.SegmentPlayer
import com.looplingua.app.player.segment.SegmentQueue
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.player.track.TrackPlayer

class MainActivity : ComponentActivity() {

    private lateinit var trackPlayer: TrackPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audioPlayer = AudioPlayer(this)
        val segmentPlayer = SegmentPlayer(audioPlayer)
        val segmentQueue = SegmentQueue(segmentPlayer)
        val sequenceBuilder = SequenceBuilder()

        trackPlayer = TrackPlayer(
            sequenceBuilder = sequenceBuilder,
            segmentQueue = segmentQueue
        )

        val track = Track(

            id = 1,

            originalResId = R.raw.greetings_uk,

            translationResId = R.raw.greetings_en,

            memoResId = R.raw.greetings_memo
        )

        val segment = Segment(

            id = 1,

            originalStartMs = 0,
            originalEndMs = 2500,

            translationStartMs = 0,
            translationEndMs = 2500,

            memoStartMs = 0,
            memoEndMs = 1500,

            originalText = "Добрий день",
            translationText = "Good afternoon",
            memoText = "formal greeting"
        )

        trackPlayer.setTrack(track)
        trackPlayer.setSegments(listOf(segment))

        setContent {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Button(
                    onClick = { trackPlayer.start() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("PLAY")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { trackPlayer.stop() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("STOP")
                }
            }
        }
    }
}