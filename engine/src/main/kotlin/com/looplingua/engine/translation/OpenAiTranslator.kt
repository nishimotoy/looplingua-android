package com.looplingua.engine.translation

import com.looplingua.engine.translation.Translator
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

// OpenAI Responses APIを利用して
// 1つの文字列を翻訳する。

class OpenAiTranslator(
    private val apiKey: String
) : Translator {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.MINUTES)
        .build()

    override fun translate(
        text: String,
        originalLang: String,
        translationLang: String
    ): String {

        val requestJson = """
{
  "model": "gpt-4.1-mini",
  "input": "Translate the following text from $originalLang to $translationLang. Output only the translation. Text: $text"
}
""".trimIndent()

        println(requestJson)

        val requestBody = requestJson
            .toRequestBody(
                "application/json; charset=utf-8".toMediaType()
            )

        // HTTP Request
        val request = Request.Builder()
            .url("https://api.openai.com/v1/responses")
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        // send
        client.newCall(request).execute().use { response ->

            if (!response.isSuccessful) {
                error(response.body?.string() ?: "Unknown error")
            }

            val json = response.body!!.string()

            val parser = Json {
                ignoreUnknownKeys = true
            }

            val translationResponse =
                parser.decodeFromString<TranslationResponse>(json)

            return translationResponse
                .output
                .first()
                .content
                .first()
                .text
        }
    }
}