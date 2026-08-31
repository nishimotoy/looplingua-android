package com.looplingua.engine.test

import com.looplingua.engine.converter.WhisperProjectConverter
import com.looplingua.engine.model.LoopLinguaProject
import com.looplingua.engine.whisper.WhisperApi
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    val inputDirectory = File("testdata/input/sample")

    val projectName = "GlobalNewsPodcast-20260620-TheHappyPodSuccessStoriesOfFormerRefugees"
    val originalLang = "english"
    
    /*⃣
    val inputDirectory = File("testdata/input/青本ウクライナ語_errors")

    val projectName = "青本ウクライナ語_errors"  // エラーの多い音源をまとめてテスト
    val originalLang = "ukrainian"
     */
    val translationLang = "Japanese"

    fun toWhisperLanguageCode(language: String): String =
        when (language.lowercase()) {
            "ukrainian" -> "uk"
            "english" -> "en"
            "japanese" -> "ja"
            "russian" -> "ru"
            else -> error("Unsupported Whisper language: $language")
        }

    // Project ID = project creation timestamp
    val projectId = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))

    // Project directory
    val projectDirectory = File(
        "testdata/project/$projectId-$projectName"
    )

    // Audio directory
    val audioDirectory = File(
        projectDirectory,
        "Audio"
    )

    // Project JSON
    val outputProject = File(
        projectDirectory,
        "$projectName.looplingua"
    )

    projectDirectory.mkdirs()
    audioDirectory.mkdirs()

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

        val destinationMp3 = File(
            audioDirectory,
            inputMp3.name
        )

        inputMp3.copyTo(
            destinationMp3,
            overwrite = true
        )

        println("[$trackId] Transcribing: ${inputMp3.name}")

        val whisperOutput = File(
            "testdata/output/whisper-${inputMp3.nameWithoutExtension}.json"
        )

        val response = whisper.transcribe(
            inputMp3,
            whisperOutput,
            language = toWhisperLanguageCode(originalLang)
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
                "Input file" to inputMp3.path,
                "Output file" to outputProject.path,
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
        projectId = projectId,
        projectName = projectName,
        tracks = tracks
    )

    //------------------------------------------------------------------
    // Save Project
    //------------------------------------------------------------------

    outputProject.parentFile.mkdirs()

    val projectJson = toPrettyJson(project)

    outputProject.writeText(projectJson)

    appendLog(
        mapOf(
            "ProjectID" to projectId,
            "ProjectName" to projectName,
            "Files" to inputFiles.joinToString(", ") { it.name }
        )
    )

    println()
    println("Project created:")
    println("  ${outputProject.path}")
    println("  Tracks: ${project.tracks.size}")
}