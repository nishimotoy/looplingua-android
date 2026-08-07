package com.looplingua.engine.test

import com.looplingua.engine.model.LoopLinguaProject
import com.looplingua.engine.translation.LoopLinguaTranslator
import com.looplingua.engine.translation.OpenAiBatchTranslator
import java.io.File

fun main() {

    val apiKey = loadApiKey()

    val translator = OpenAiBatchTranslator(apiKey)

    val projectTranslator =
        LoopLinguaTranslator(translator)

    val projectDirectory = File(
        "testdata/project/1785819205167-青本ウクライナ語"
    )

    val inputProject = File(
        projectDirectory,
        "青本ウクライナ語.looplingua"
    )

    val json = parserJson()

    val project =
        json.decodeFromString<LoopLinguaProject>(
            inputProject.readText()
        )

    val translatedProject =
        projectTranslator.translate(project)

    val outputProject = File(
        projectDirectory,
        "${project.projectName}.looplingua"
    )

    val projectJson = toPrettyJson(translatedProject)

    outputProject.writeText(projectJson)

    val inputSegments =
        project.tracks.sumOf { it.segments.size }

    val outputSegments =
        translatedProject.tracks.sumOf { it.segments.size }

    appendLog(
        mapOf(
            "Input file" to inputProject.path,
            "Output file" to outputProject.path,
            "Model" to "gpt-4.1-mini",
            "Input Tracks" to project.tracks.size.toString(),
            "Output Tracks" to translatedProject.tracks.size.toString(),
            "Input Segments" to inputSegments.toString(),
            "Output Segments" to outputSegments.toString()
        )
    )
}