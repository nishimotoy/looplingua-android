package com.looplingua.engine.translation

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class OpenAiBatchTranslator(
    private val apiKey: String
) : BatchTranslator {

    private val client = OkHttpClient()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Serializable
    private data class TranslationResponse(
        val translations: List<String>
    )

    override fun translate(
        texts: List<String>,
        originalLang: String,
        translationLang: String
    ): List<String> {

        if (texts.isEmpty()) {
            return emptyList()
        }

        val numberedTexts =
            texts.mapIndexed { index, text ->
                "$index: $text"
            }.joinToString("\n")

        val prompt = """
            Translate the following texts from $originalLang to $translationLang.

            Each numbered line is one independent segment.
            Preserve the exact order and return exactly one translation for each input segment.

            Return JSON only in this format:
            {"translations":["translation 0","translation 1",...]}

            Texts:
            $numberedTexts
        """.trimIndent()

        val requestBody = """
            {
              "model": "gpt-4.1-mini",
              "input": ${json.encodeToString(prompt)}
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("https://api.openai.com/v1/responses")
            .addHeader(
                "Authorization",
                "Bearer $apiKey"
            )
            .addHeader(
                "Content-Type",
                "application/json"
            )
            .post(
                requestBody.toRequestBody(
                    "application/json".toMediaType()
                )
            )
            .build()

        client.newCall(request).execute().use { response ->

            if (!response.isSuccessful) {
                throw RuntimeException(
                    "OpenAI API error: ${response.code} ${response.message}"
                )
            }

            val responseBody =
                response.body?.string()
                    ?: throw RuntimeException(
                        "OpenAI API returned an empty response."
                    )

            // Responses APIのoutputから
            // 実際のJSON文字列を取り出す処理は、
            // 現在のOpenAiTranslatorの実装に合わせて
            // 次の段階で整理する。
            TODO("Parse Responses API output")
        }
    }
}