package com.looplingua.engine.test

import com.looplingua.engine.model.LoopLinguaProject
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

fun loadApiKey(): String {

    val properties = Properties().apply {
        load(File("local.properties").inputStream())
    }

    val apiKey = properties.getProperty("OPENAI_API_KEY")
        ?: error("OPENAI_API_KEY not found")

    return apiKey
}

fun prettyJson() = Json {
    prettyPrint = true
}

fun toPrettyJson(project: LoopLinguaProject): String =
    Json {
        prettyPrint = true
    }.encodeToString(project)

fun parserJson() = Json {
    ignoreUnknownKeys = true
}

fun appendLogWhisper() =  {

}

fun appendLogTranslation(
    inputFileName: String,
    outputFileName: String,
    inputSegmentsSize: Int,
    outputSegmentsSize: Int
)  {

    val logFile = File("testdata/output/engine.log")

    val now = LocalDateTime.now()

    val formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    logFile.appendText(
        """
==================================================
${now.format(formatter)}
Input file      : ${inputFileName}
Output file     : ${outputFileName}
Model           : gpt-4.1-mini
Input Segments  : ${inputSegmentsSize}
Output Segments : ${outputSegmentsSize}
==================================================

""".trimIndent() + "\n"
    )
}

fun appendLogWhisper(
    inputFileName: String,
    outputFileName: String,
    duration: String,
    outputSegmentsSize: Int
)  {

    val logFile = File("testdata/output/engine.log")

    val now = LocalDateTime.now()

    val formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    logFile.appendText(
        """
==================================================
${now.format(formatter)}
Input file      : ${inputFileName}
Output file     : ${outputFileName}
Model           : gpt-4.1-mini
Duration        : ${duration}
Output Segments : ${outputSegmentsSize}
==================================================

""".trimIndent() + "\n"
    )
}
