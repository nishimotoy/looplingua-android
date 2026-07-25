package com.looplingua.engine.test

import com.looplingua.engine.translation.OpenAiTranslator
import java.io.File
import java.util.Properties

private fun main() {

    val properties = Properties().apply {
        load(File("local.properties").inputStream())
    }

    val apiKey = properties.getProperty("OPENAI_API_KEY")
        ?: error("OPENAI_API_KEY not found")

    val translator = OpenAiTranslator(apiKey)

    val result = translator.translate(
        text = "Hello",
        sourceLanguage = "English",
        targetLanguage = "Japanese"
    )

    println(result)
}