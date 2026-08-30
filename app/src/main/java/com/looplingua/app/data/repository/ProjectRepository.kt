package com.looplingua.app.data.repository

import com.looplingua.app.data.storage.ProjectStorage
import com.looplingua.app.ui.project.ProjectItem
import java.io.File

class ProjectRepository(
    private val projectStorage: ProjectStorage
) {

    fun listProjects(): List<File> {
        return projectStorage.projectsDirectory
            .listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name }
            ?: emptyList()
    }

    fun listProjectItems(): List<ProjectItem> {
        return listProjects().mapNotNull { directory ->

            val parts = directory.name.split("-", limit = 2)

            if (parts.size != 2) return@mapNotNull null

            ProjectItem(
                projectId = parts[0],
                projectName = parts[1],
                directoryPath = directory.absolutePath
            )
        }
    }
}
