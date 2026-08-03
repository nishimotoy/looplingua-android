package com.looplingua.engine.test

import com.looplingua.engine.converter.WhisperProjectConverter
import com.looplingua.engine.model.LoopLinguaProject
import com.looplingua.engine.whisper.WhisperApi
import java.io.File

private fun formatDuration(seconds: Double): String {
    val totalSeconds = seconds.toInt()
    val minutes = totalSeconds / 60
    val secs = totalSeconds % 60
    return "%d:%02d".format(minutes, secs)
}

fun main() {

    //------------------------------------------------------------------
    // Project settings
    //------------------------------------------------------------------

    val inputDirectory = File("testdata/input/青本ウクライナ語")
    val outputProject = File(
        "testdata/output/青本ウクライナ語.looplingua"
    )

    val projectName = "青本ウクライナ語"
    val translationLang = "Japanese"

    //------------------------------------------------------------------
    // Input files
    //------------------------------------------------------------------

    val inputFiles = inputDirectory
        .listFiles { file ->        // ファイル一覧取得
            file.isFile &&
                    file.extension.equals("mp3", ignoreCase = true)
        }
        ?.sortedBy { it.name }      // it は file
        ?: emptyList()

    require(inputFiles.isNotEmpty()) {
        "No mp3 files found in: ${inputDirectory.path}"
    }

    println("Input files: ${inputFiles.size}")

    //------------------------------------------------------------------
    // API / converter
    //------------------------------------------------------------------

    val apiKey = loadApiKey()

    val whisper = WhisperApi(apiKey)
    val converter = WhisperProjectConverter()

    //------------------------------------------------------------------
    // Transcribe each file and create Track
    //------------------------------------------------------------------

    val tracks = inputFiles.mapIndexed { trackId, inputMp3 ->  // リストの全要素に処理

        println("[$trackId] Transcribing: ${inputMp3.name}")

        val whisperOutput = File(
            "testdata/output/whisper-${inputMp3.nameWithoutExtension}.json"
        )

        val response = whisper.transcribe(
            inputMp3,
            whisperOutput
        )

        val duration =
            response.segments.lastOrNull()?.end ?: 0.0

        val track = converter.convert(
            response = response,
            audioPath = inputMp3.path,
            trackId = trackId,
            translationLang = translationLang
        )

        appendLog(
            mapOf(
                "Input file" to inputMp3.name,
                "Output file" to outputProject.name,
                "Model" to "whisper-1",
                "Duration" to formatDuration(duration),
                "Output Segments" to response.segments.size.toString()
            )
        )

        println(
            "[$trackId] ${inputMp3.name}: " +
                    "${response.segments.size} segments"
        )

        track
    }

    //------------------------------------------------------------------
    // Create Project
    //------------------------------------------------------------------

    val project = LoopLinguaProject(
        projectId = System.currentTimeMillis().toString(),
        projectName = projectName,
        tracks = tracks
    )

    //------------------------------------------------------------------
    // Save Project
    //------------------------------------------------------------------

    outputProject.parentFile.mkdirs()

    val projectJson = toPrettyJson(project)

    outputProject.writeText(projectJson)

    println()
    println("Project created:")
    println("  ${outputProject.path}")
    println("  Tracks: ${project.tracks.size}")
}