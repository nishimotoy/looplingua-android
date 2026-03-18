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
import com.looplingua.app.ui.mapper.TrackUiMapper


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val trackDataList = listOf(
            SegmentFileLoader.loadFromAssets(this, "testdata/tracks/1/track.json"),
            SegmentFileLoader.loadFromAssets(this, "testdata/tracks/2/track.json")
        )

        val controller = createPlayerController(trackDataList.first().track)

        // 再生用
        val allSegments = trackDataList.flatMap { it.segments }
        controller.setSegments(allSegments)

        val items = TrackUiMapper.buildItems(trackDataList)

        setContent {
            MainScreen(
                controller = controller,
                items = items
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