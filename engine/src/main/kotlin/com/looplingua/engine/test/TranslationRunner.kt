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

    appendLogTranslation(
        inputProject.name,
        outputProject.name,
        project.tracks.first().segments.size,
        translatedProject.tracks.first().segments.size
    )
}