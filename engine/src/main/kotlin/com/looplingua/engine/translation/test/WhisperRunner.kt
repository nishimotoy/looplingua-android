package com.looplingua.engine.translation.test

import com.looplingua.engine.whisper.WhisperApi
import java.io.File
import java.util.Properties

fun main() {

    val inputMp3 = File("testdata/input/sample.mp3")
    val outputJson = File("testdata/output/whisper.json")

    val properties = Properties().apply {
        load(File("local.properties").inputStream())
    }

    val apiKey = properties.getProperty("OPENAI_API_KEY")
        ?: error("OPENAI_API_KEY not found")

    println("API Key loaded.")
    println(inputMp3.absolutePath)
    println(outputJson.absolutePath)

    val whisper = WhisperApi(apiKey)

    val response = whisper.transcribe(
        inputMp3,
        outputJson
    )

    println("Transcription completed.")
    println("${response.segments.size} segments")

}