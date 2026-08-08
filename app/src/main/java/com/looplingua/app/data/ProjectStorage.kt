package com.looplingua.app.data.storage

import android.content.Context
import java.io.File

class ProjectStorage(
    private val context: Context
) {

    val projectsDirectory: File
        get() = File(
            context.filesDir,
            "projects"
        ).apply {
            mkdirs()
        }
}