package com.looplingua.engine.test

import com.looplingua.engine.model.LoopLinguaProject
import com.looplingua.engine.translation.LoopLinguaTranslator
import com.looplingua.engine.translation.OpenAiTranslator
import java.io.File

private fun main() {

    val apiKey = loadApiKey()

    val translator = OpenAiTranslator(apiKey)

    val projectTranslator =
        LoopLinguaTranslator(translator)

    val inputProject = File(
        "testdata/output/sample.looplingua"
    )

    val json = parserJson()

    val project =
        json.decodeFromString<LoopLinguaProject>(
            inputProject.readText()
        )

    val translatedProject =
        projectTranslator.translate(project)

    val outputProject = File(
        "testdata/output/${project.projectName}_ja.looplingua"
    )

    val projectJson = toPrettyJson(translatedProject)

    outputProject.writeText(projectJson)

    appendLog(
        mapOf(
            "Input file" to inputProject.name,
            "Output file" to outputProject.name,
            "Model" to "gpt-4.1-mini",
            "Input Segments" to project.tracks.first().segments.size.toString(),
            "Output Segments" to translatedProject.tracks.first().segments.size.toString()
        )
    )
}