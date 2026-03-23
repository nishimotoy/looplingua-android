package com.looplingua.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.looplingua.app.data.repository.TrackRepository
import com.looplingua.app.domain.model.TrackWithSegments
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.factory.PlayerFactory
import com.looplingua.app.ui.MainScreen
import com.looplingua.app.ui.mapper.TrackUiMapper
import com.looplingua.app.ui.theme.LoopLinguaandroidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = TrackRepository(this)
        val trackDataList = repository.loadInitialTracks()

        val controller = PlayerFactory.create(
            this,
            trackDataList.first().track
        )

        // 再生用
        val tracks = trackDataList.map {
            TrackWithSegments(it.track, it.segments)
        }

        controller.setTracks(tracks)

        val items = TrackUiMapper.buildItems(trackDataList)

        setContent {
            LoopLinguaandroidTheme {
                MainScreen(
                    controller = controller,
                    items = items
                )
            }
        }

        controller.setPattern(Pattern.ORIGINAL_ONLY)
        controller.play() // 起動時に再生　デバッグ用
    }
}