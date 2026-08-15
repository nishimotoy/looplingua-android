package com.looplingua.app.data.repository

import com.looplingua.app.data.looplingua.LoopLinguaProjectLoader
import com.looplingua.app.data.looplingua.PlayerProjectMapper
import com.looplingua.app.domain.model.TrackWithSegments
import kotlinx.serialization.json.Json
import java.io.File

class TrackRepository {

    private val projectLoader = LoopLinguaProjectLoader()
    private val projectMapper = PlayerProjectMapper()

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    fun loadProject(
        projectDirectory: File
    ): List<TrackWithSegments> {

        val projectFile = File(
            projectDirectory,
            "${projectDirectory.name.substringAfter("-")}.looplingua"
        )

        val project = projectLoader.load(projectFile)

        return projectMapper.map(
            project = project,
            projectDirectory = projectDirectory
        )
    }

    /**
     * Player上で変更されたflaggedだけを
     * .looplinguaへ保存する。
     *
     * 元のProjectを読み直してからflaggedだけを差し替えるため、
     * originalAuto / originalUser / translationAuto /
     * translationUser / memoAuto / memoUser など、
     * Player側が直接扱っていないデータを変更しない。
     */
    fun saveFlags(
        projectDirectory: File,
        tracks: List<TrackWithSegments>
    ) {

        val projectFile = File(
            projectDirectory,
            "${projectDirectory.name.substringAfter("-")}.looplingua"
        )

        require(projectFile.isFile) {
            "Project file not found: ${projectFile.path}"
        }

        val project = projectLoader.load(projectFile)

        val updatedTracks = project.tracks.map { projectTrack ->

            val playerTrack = tracks.firstOrNull {
                it.track.id == projectTrack.trackId
            }

            if (playerTrack == null) {
                projectTrack
            } else {

                val updatedSegments =
                    projectTrack.segments.map { projectSegment ->

                        val playerSegment =
                            playerTrack.segments.firstOrNull {
                                it.id == projectSegment.segmentId
                            }

                        if (playerSegment == null) {
                            projectSegment
                        } else {
                            projectSegment.copy(
                                flagged = playerSegment.flagged
                            )
                        }
                    }

                projectTrack.copy(
                    segments = updatedSegments
                )
            }
        }

        val updatedProject =
            project.copy(
                tracks = updatedTracks
            )

        val jsonText =
            json.encodeToString(updatedProject)

        /*
         * いきなり本体へwriteText()すると、
         * 書き込み途中でアプリが終了した場合に
         * .looplinguaが壊れる可能性がある。
         *
         * そのため、まず一時ファイルへ書き込む。
         */
        val tempFile = File(
            projectFile.parentFile,
            "${projectFile.name}.tmp"
        )

        tempFile.writeText(jsonText)

        /*
         * 同じディレクトリ内のファイルを置き換える。
         */
        if (!tempFile.renameTo(projectFile)) {

            tempFile.delete()

            error(
                "Failed to replace project file: ${projectFile.path}"
            )
        }
    }
}