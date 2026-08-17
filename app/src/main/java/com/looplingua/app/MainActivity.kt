package com.looplingua.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.looplingua.app.data.repository.TrackRepository
import com.looplingua.app.data.storage.ProjectStorage
import com.looplingua.app.player.factory.PlayerFactory
import com.looplingua.app.ui.MainScreen
import com.looplingua.app.ui.mapper.TrackUiMapper
import com.looplingua.app.ui.theme.LoopLinguaandroidTheme
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MainActivity : ComponentActivity() {

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

        controller.setTracks(tracks)

        val items =
            TrackUiMapper.buildItems(tracks)

        setContent {
            LoopLinguaandroidTheme(darkTheme = false) {
                MainScreen(
                    controller = controller,
                    items = items
                )
            }
        }

        controller.play() // 起動時に再生　デバッグ用
    }
}