package com.looplingua.engine.test

import com.looplingua.engine.converter.WhisperProjectConverter
import com.looplingua.engine.whisper.WhisperApi
import java.io.File
import java.util.Properties
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

    println(outputProject.absolutePath)
    println("LoopLingua project created.")
    println("${response.segments.size} segments")

}