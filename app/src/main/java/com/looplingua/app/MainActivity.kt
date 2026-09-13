package com.looplingua.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.looplingua.app.data.repository.ProjectRepository
import com.looplingua.app.data.repository.TrackRepository
import com.looplingua.app.data.storage.ProjectStorage
import com.looplingua.app.domain.model.SegmentKey
import com.looplingua.app.domain.model.TrackWithSegments
import com.looplingua.app.player.controller.PlayerController
import com.looplingua.app.player.factory.PlayerFactory
import com.looplingua.app.ui.MainScreen
import com.looplingua.app.ui.project.ProjectItem
import com.looplingua.app.ui.theme.LoopLinguaandroidTheme
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MainActivity : ComponentActivity() {

    private lateinit var controller: PlayerController
    private val flagSaveMutex = Mutex()

    private var isInitialized by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lateinit var projectDirectory: File
        lateinit var projectId: String

        val repository = TrackRepository()
        val projectRepository = ProjectRepository(
            ProjectStorage(this),
            repository
        )

        // 起動時のデータ読み込みを非同期で行うための、一時的な保持変数
        var projects = emptyList<ProjectItem>()
        var tracksByProject =
            emptyMap<String, List<TrackWithSegments>>()

        fun setCurrentProject(project: ProjectItem): List<TrackWithSegments> {
            projectDirectory = File(project.directoryPath)
            projectId = project.projectId
            return projectRepository.listTracks(project)
        }

        // フラグ反映・更新処理の初期化
        controller = PlayerFactory.create(
            context = this,
            saveFlags = { updatedTracks ->
                lifecycleScope.launch(Dispatchers.IO) {
                    flagSaveMutex.withLock {
                        repository.saveFlags(
                            projectDirectory = projectDirectory,
                            tracks = updatedTracks
                        )
                    }
                }
            }
        )

        // 起動時のデータ読み込み ＆ 前回再生位置の復元
        lifecycleScope.launch {
            // 1. プロジェクト一覧とマップを生成
            projects = projectRepository.listProjectItems()
            tracksByProject = projects.associate { project ->
                project.projectId to projectRepository.listTracks(project)
            }

            // 2. 前回位置の復元
            val lastPlaybackPosition = controller.getLastPlaybackPosition()
            val welcomeProject = projects.firstOrNull {
                it.projectName.equals("Welcome", ignoreCase = true)
            }
            val project = projects.firstOrNull {
                it.projectId == lastPlaybackPosition?.projectId
            } ?: welcomeProject ?: return@launch

            val tracks = setCurrentProject(project)
            controller.setProjectId(projectId)
            controller.setTracks(tracks)
            controller.restorePlaybackPosition()

            // 3. データ準備完了を通知（画面を描画させる）
            isInitialized = true
            controller.play()
        }

        // UI 描画
        setContent {
            LoopLinguaandroidTheme(darkTheme = false) {
                if (isInitialized) {
                    MainScreen(
                        controller = controller,
                        projects = projects,
                        tracksByProject = tracksByProject,
                        onProjectSelected = { project ->
                            if (project.projectId != projectId) {
                                controller.stop()
                                val selectedTracks = setCurrentProject(project)
                                controller.setProjectId(projectId)
                                controller.setTracks(selectedTracks)
                                controller.play()
                            }
                        },
                        onTrackSelected = { project, track ->
                            val selectedTracks = setCurrentProject(project)
                            val firstSegment = track.segments.firstOrNull()

                            if (firstSegment != null) {
                                controller.setProjectId(projectId)
                                controller.setTracks(selectedTracks)
                                controller.playFrom(
                                    SegmentKey(
                                        trackId = track.track.id,
                                        segmentId = firstSegment.id
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        controller.release()
        super.onDestroy()
    }
}