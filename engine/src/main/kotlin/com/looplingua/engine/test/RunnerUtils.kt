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

fun toPrettyJson(project: LoopLinguaProject): String =
    Json {
        prettyPrint = true
    }.encodeToString(project)

fun parserJson() = Json {
    ignoreUnknownKeys = true
}

fun appendLog(data: Map<String, String>) {

    val logFile = File("testdata/output/engine.log")

    val now = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    val body = data.entries.joinToString("\n") { (key, value) ->
        "%-15s : %s".format(key, value)
    }

    logFile.appendText(
        """
==================================================
$now
$body
==================================================

""".trimIndent() + "\n"
    )
}
