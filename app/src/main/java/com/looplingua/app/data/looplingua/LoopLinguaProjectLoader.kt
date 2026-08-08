package com.looplingua.app.data.looplingua

import kotlinx.serialization.json.Json
import java.io.File

class LoopLinguaProjectLoader {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun load(file: File): LoopLinguaProject {
        return json.decodeFromString(
            file.readText()
        )
    }
}