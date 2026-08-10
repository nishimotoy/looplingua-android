package com.looplingua.engine.test

import com.looplingua.engine.model.LoopLinguaProject
import com.looplingua.engine.translation.LoopLinguaTranslator
import com.looplingua.engine.translation.OpenAiBatchTranslator
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {

    val apiKey = loadApiKey()

    val translator = OpenAiBatchTranslator(apiKey)

    val projectTranslator =
        LoopLinguaTranslator(translator)

    //------------------------------------------------------------------
    // Project selection
    //------------------------------------------------------------------

    val projectRoot = File("testdata/project")

    val projectIdFormatter =
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

    val projectDirectory =
        projectRoot.listFiles { file ->
            file.isDirectory &&
                    Regex("""^\d{14}-.+$""").matches(file.name)
        }
            ?.mapNotNull { directory ->

                val projectId =
                    directory.name.substringBefore("-")

                try {
                    val createdAt =
                        LocalDateTime.parse(
                            projectId,
                            projectIdFormatter
                        )

                    directory to createdAt

                } catch (_: Exception) {
                    null
                }
            }
            ?.maxByOrNull { (_, createdAt) -> createdAt }
            ?.first
            ?: error(
                "No valid project directory found in: " +
                        projectRoot.path
            )

    println(
        "Selected project: ${projectDirectory.name}"
    )

    //------------------------------------------------------------------
    // Input project
    //------------------------------------------------------------------

    val inputProject =
        projectDirectory.listFiles { file ->
            file.isFile &&
                    file.extension.equals(
                        "looplingua",
                        ignoreCase = true
                    )
        }
            ?.singleOrNull()
            ?: error(
                "Exactly one .looplingua file is expected in: " +
                        projectDirectory.path
            )

    //------------------------------------------------------------------
    // Load project
    //------------------------------------------------------------------

    val json = parserJson()

    val project =
        json.decodeFromString<LoopLinguaProject>(
            inputProject.readText()
        )

    //------------------------------------------------------------------
    // Translate
    //------------------------------------------------------------------

    val translatedProject =
        projectTranslator.translate(project)

    //------------------------------------------------------------------
    // Save project
    //------------------------------------------------------------------

    val outputProject = File(
        projectDirectory,
        "${project.projectName}.looplingua"
    )

    val projectJson =
        toPrettyJson(translatedProject)

    outputProject.writeText(projectJson)

    //------------------------------------------------------------------
    // Log
    //------------------------------------------------------------------

    val inputSegments =
        project.tracks.sumOf {
            it.segments.size
        }

    val outputSegments =
        translatedProject.tracks.sumOf {
            it.segments.size
        }

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