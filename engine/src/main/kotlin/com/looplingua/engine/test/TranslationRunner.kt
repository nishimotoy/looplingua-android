package com.looplingua.engine.test

import com.looplingua.engine.model.LoopLinguaProject
import com.looplingua.engine.translation.LoopLinguaTranslator
import com.looplingua.engine.translation.OpenAiTranslator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Properties

private fun main() {

    val properties = Properties().apply {
        load(File("local.properties").inputStream())
    }

    val apiKey = properties.getProperty("OPENAI_API_KEY")
        ?: error("OPENAI_API_KEY not found")

    val translator = OpenAiTranslator(apiKey)

    val projectTranslator =
        LoopLinguaTranslator(translator)

    val inputProject = File(
        "testdata/output/sample.looplingua"
    )

    val json = Json {
        ignoreUnknownKeys = true
    }

    val project =
        json.decodeFromString<LoopLinguaProject>(
            inputProject.readText()
        )

    val translatedProject =
        projectTranslator.translate(project)

    println(project.projectName)
    println(project.tracks.size)
    println(project.tracks.first().segments.size)

}