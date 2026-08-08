package com.looplingua.app.data.repository

import com.looplingua.app.data.looplingua.LoopLinguaProjectLoader
import com.looplingua.app.data.looplingua.PlayerProjectMapper
import com.looplingua.app.domain.model.TrackWithSegments
import java.io.File

class TrackRepository {

    private val projectLoader = LoopLinguaProjectLoader()
    private val projectMapper = PlayerProjectMapper()

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
}