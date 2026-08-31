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

    /*
     * FLAG保存はUI操作とは別にバックグラウンドで行う。
     *
     * FLAGを短時間に連続して変更した場合でも、
     * .looplinguaへの書き込みが同時実行されて
     * 新しい状態が古い状態で上書きされないようにする。
     */
    private val flagSaveMutex = Mutex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var projectDirectory = File(
            ProjectStorage(this).projectsDirectory,
            "20260812010803-青本ウクライナ語"  // 100本テスト
        )

        /*
         * Project IDは、
         * 「プロジェクトID-プロジェクト名」
         * というディレクトリ名の先頭部分から取得する。
         */
        var projectId =
            projectDirectory.name.substringBefore("-")

        val repository = TrackRepository()

        val tracks = repository.loadProject(
            projectDirectory
        )

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

                /*
                 * FLAGのUI状態はPlayerController側ですでに更新済み。
                 *
                 * ここではファイル保存だけをIOスレッドへ移す。
                 * そのため、FLAGボタンを押した直後にUIへ
                 * FLAGGED / FLAG の変更が反映される。
                 */
                lifecycleScope.launch(Dispatchers.IO) {

                    /*
                     * 保存処理を直列化する。
                     *
                     * TrackRepository.saveFlags() は
                     * .looplinguaを読み直してから書き換えるため、
                     * 複数の保存処理を同時実行させない。
                     */
                    flagSaveMutex.withLock {

                        repository.saveFlags(
                            projectDirectory = projectDirectory,
                            tracks = updatedTracks
                        )
                    }
                }
            }
        )

        /*
         * PlayerControllerに現在のProject IDを設定する。
         * 再生位置は
         * Project + Track + Segment
         * の組み合わせで特定する。
         */
        controller.setProjectId(projectId)
        controller.setTracks(tracks)

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

        controller.play() // 起動時に再生　デバッグ用
    }

    override fun onDestroy() {
        controller.release()
        super.onDestroy()
    }
}