package com.looplingua.engine.test

import com.looplingua.engine.converter.WhisperProjectConverter
import com.looplingua.engine.whisper.WhisperApi
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private fun formatDuration(seconds: Double): String {
    val totalSeconds = seconds.toInt()
    val minutes = totalSeconds / 60
    val secs = totalSeconds % 60
    return "%d:%02d".format(minutes, secs)
}

fun main() {

    val inputMp3 = File("testdata/input/sample.mp3")
    val outputJson = File("testdata/output/whisper.json")

    val logFile = File("testdata/output/engine.log")

    val now = LocalDateTime.now()

    val formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    val properties = Properties().apply {
        load(File("local.properties").inputStream())
    }

    val apiKey = properties.getProperty("OPENAI_API_KEY")
        ?: error("OPENAI_API_KEY not found")

    println("API Key loaded.")
    println("Input file : ${inputMp3.name}")

    println(inputMp3.absolutePath)
    println(outputJson.absolutePath)

    val whisper = WhisperApi(apiKey)

    val response = whisper.transcribe(
        inputMp3,
        outputJson
    )

    val duration =
        response.segments.lastOrNull()?.end ?: 0.0

    println("Duration   : ${formatDuration(duration)}")
    println("Model      : whisper-1")

    val converter = WhisperProjectConverter()

    val project = converter.convert(
        response = response,
        audioPath = inputMp3.path
    )

    val outputProject = File(
        "testdata/output/${inputMp3.nameWithoutExtension}.looplingua"
    )

    val projectJson = Json {
        prettyPrint = true
    }.encodeToString(project)

    outputProject.writeText(projectJson)

    logFile.appendText(
        """
==================================================
${now.format(formatter)}
Input file : ${inputMp3.name}
Duration   : ${formatDuration(duration)}
Model      : whisper-1
Segments   : ${response.segments.size}
Output     : ${outputProject.name}
==================================================

""".trimIndent() + "\n"
    )

    println(outputProject.absolutePath)
    println("LoopLingua project created.")
    println("${response.segments.size} segments")

}