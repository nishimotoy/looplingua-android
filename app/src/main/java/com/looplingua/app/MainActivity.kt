package com.looplingua.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.looplingua.app.data.SegmentFileLoader
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

        val trackId = 1

        // ① JSONロード
        val data = SegmentFileLoader.loadFromAssets(
            this,
            "testdata/tracks/1/track.json"
        )

        // ② PlayerController作成
        val controller = createPlayerController(data.track)

        controller.setSegments(data.segments)

        // ③ UI起動
        setContent {
            MainScreen(
                controller = controller,
                segments = data.segments
            )
        }
    }

    private fun createPlayerController(track: Track): PlayerController {

        val audioPlayer = AudioPlayer(this)
        val segmentPlayer = SegmentPlayer(audioPlayer)
        val playlist = SegmentPlaylist()
        val sequenceBuilder = SequenceBuilder()

        return PlayerController(
            track = track,
            playlist = playlist,
            sequenceBuilder = sequenceBuilder,
            segmentPlayer = segmentPlayer
        )
    }
}