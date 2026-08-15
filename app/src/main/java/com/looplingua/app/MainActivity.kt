package com.looplingua.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.looplingua.app.data.repository.TrackRepository
import com.looplingua.app.data.storage.ProjectStorage
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.factory.PlayerFactory
import com.looplingua.app.ui.MainScreen
import com.looplingua.app.ui.mapper.TrackUiMapper
import com.looplingua.app.ui.theme.LoopLinguaandroidTheme
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val projectDirectory = File(
            ProjectStorage(this).projectsDirectory,
            "20260812010803-青本ウクライナ語"  // 100本テスト
        )

        val repository = TrackRepository()

        val tracks = repository.loadProject(
            projectDirectory
        )

        val controller = PlayerFactory.create(
            context = this,
            saveFlags = { updatedTracks ->
                repository.saveFlags(
                    projectDirectory = projectDirectory,
                    tracks = updatedTracks
                )
            }
        )

        controller.setTracks(tracks)

        val items = TrackUiMapper.buildItems(tracks)

        setContent {
            LoopLinguaandroidTheme(darkTheme = false) {
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