package com.looplingua.engine.test

import com.looplingua.engine.converter.WhisperProjectConverter
import com.looplingua.engine.whisper.WhisperApi
import java.io.File

private fun formatDuration(seconds: Double): String {
    val totalSeconds = seconds.toInt()
    val minutes = totalSeconds / 60
    val secs = totalSeconds % 60
    return "%d:%02d".format(minutes, secs)
}

fun main() {

    val inputMp3 = File("testdata/input/sample.mp3")
    val outputJson = File("testdata/output/whisper.json")

    val apiKey = loadApiKey()

    val whisper = WhisperApi(apiKey)

    val response = whisper.transcribe(
        inputMp3,
        outputJson
    )

    val duration =
        response.segments.lastOrNull()?.end ?: 0.0

    val converter = WhisperProjectConverter()

    val project = converter.convert(
        response = response,
        audioPath = inputMp3.path
    )

    val outputProject = File(
        "testdata/output/${inputMp3.nameWithoutExtension}.looplingua"
    )

    val projectJson = toPrettyJson(project)

    outputProject.writeText(projectJson)

    appendLog(
        mapOf(
            "Input file" to inputMp3.name,
            "Output file" to outputProject.name,
            "Model" to "whisper-1",
            "Duration" to formatDuration(duration),
            "Output Segments" to response.segments.size.toString()
        )
    )
}