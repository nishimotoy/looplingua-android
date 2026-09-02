package com.looplingua.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.looplingua.app.data.repository.ProjectRepository
import com.looplingua.app.data.repository.TrackRepository
import com.looplingua.app.data.storage.ProjectStorage
import com.looplingua.app.domain.model.SegmentKey
import com.looplingua.app.player.controller.PlayerController
import com.looplingua.app.player.factory.PlayerFactory
import com.looplingua.app.ui.MainScreen
import com.looplingua.app.ui.theme.LoopLinguaandroidTheme
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MainActivity : ComponentActivity() {

    private lateinit var controller: PlayerController

    private val flagSaveMutex = Mutex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var projectDirectory = File(
            ProjectStorage(this).projectsDirectory,
            "20260812010803-青本ウクライナ語"  // 将来は Welcome Projectを置く
        )

        var projectId =
            projectDirectory.name.substringBefore("-")

        val repository = TrackRepository()

        val projectRepository = ProjectRepository(
            ProjectStorage(this),
            repository
        )

        val projects =
            projectRepository.listProjectItems()

        val tracksByProject =
            projects.associate { project ->
                project.projectId to
                        projectRepository.listTracks(project)
            }

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

        lifecycleScope.launch {
            val lastPlaybackPosition =
                controller.getLastPlaybackPosition()

            val project =
                projects.firstOrNull {
                    it.projectId ==
                            lastPlaybackPosition?.projectId
                } ?: projects.firstOrNull()
                ?: return@launch

            projectDirectory =
                File(project.directoryPath)

            projectId =
                project.projectId

            val tracks =
                projectRepository.listTracks(project)

            controller.setProjectId(projectId)
            controller.setTracks(tracks)
            controller.play()
        }

        setContent {
            LoopLinguaandroidTheme(darkTheme = false) {
                MainScreen(
                    controller = controller,
                    projects = projects,
                    tracksByProject = tracksByProject,
                    onProjectSelected = { project ->
                        projectDirectory = File(project.directoryPath)
                        projectId = project.projectId

                        val selectedTracks =
                            projectRepository.listTracks(project)

                        controller.setProjectId(projectId)
                        controller.setTracks(selectedTracks)
                        controller.play()
                    },
                    onTrackSelected = { project, track ->
                        projectDirectory = File(project.directoryPath)
                        projectId = project.projectId

                        val selectedTracks =
                            projectRepository.listTracks(project)

                        val firstSegment =
                            track.segments.firstOrNull()

                        if (firstSegment != null) {
                            controller.setProjectId(projectId)

                            controller.setTracksAndPlayFrom(
                                tracks = selectedTracks,
                                key = SegmentKey(
                                    trackId = track.track.id,
                                    segmentId = firstSegment.id
                                )
                            )
                        }
                    }
                )
            }
        }

        // controller.play() // 起動時に再生　デバッグ用
    }

    override fun onDestroy() {
        controller.release()
        super.onDestroy()
    }
}