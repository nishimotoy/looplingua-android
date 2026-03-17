package com.looplingua.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.looplingua.app.data.SegmentFileLoader
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.player.audio.AudioPlayer
import com.looplingua.app.player.controller.PlayerController
import com.looplingua.app.player.segment.SegmentPlaylist
import com.looplingua.app.player.segment.SegmentPlayer
import com.looplingua.app.player.sequence.SequenceBuilder
import com.looplingua.app.ui.track.TrackListItem
import com.looplingua.app.ui.track.TrackScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ① Track1
        val data1 = SegmentFileLoader.loadFromAssets(
            this,
            "testdata/tracks/1/track.json"
        )

        // ② Track2
        val data2 = SegmentFileLoader.loadFromAssets(
            this,
            "testdata/tracks/2/track.json"
        )

        val controller = createPlayerController(data1.track)

        // ★ TrackQueueテスト用（2トラック追加）
        controller.setSegments(data1.segments + data2.segments)

        // ★ UI用リスト作成
        val items = buildTrackItems(
            data1.segments,
            data2.segments
        )

        setContent {
            TrackScreen(
                controller = controller,
                items = items
            )
        }
    }

    private fun buildTrackItems(
        segments1: List<Segment>,
        segments2: List<Segment>
    ): List<TrackListItem> {

        val items = mutableListOf<TrackListItem>()

        var index = 0

        items.add(TrackListItem.TrackHeader("Track 1"))
        segments1.forEach { segment ->
            items.add(TrackListItem.SegmentItem(segment, index))
            index++
        }

        items.add(TrackListItem.TrackHeader("Track 2"))
        segments2.forEach { segment ->
            items.add(TrackListItem.SegmentItem(segment, index))
            index++
        }

        return items
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