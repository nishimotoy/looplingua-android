package com.looplingua.engine.translation

import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class OpenAiBatchTranslator(
    private val apiKey: String
) : BatchTranslator {

    companion object {
        private const val MAX_RETRIES = 3   // リトライ導入
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.MINUTES)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
    }

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

        repeat(MAX_RETRIES + 1) { attempt ->

            try {

                client.newCall(request).execute().use { response ->

                    if (!response.isSuccessful) {
                        throw RuntimeException(
                            "OpenAI API error: " +
                                    "${response.code} ${response.message}"
                        )
                    }

                    val responseBody =
                        response.body?.string()
                            ?: throw RuntimeException(
                                "OpenAI API returned an empty response."
                            )

                    val parser = Json {
                        ignoreUnknownKeys = true
                    }

                    val translationResponse =
                        parser.decodeFromString<TranslationResponse>(
                            responseBody
                        )

                    val outputText =
                        translationResponse
                            .output
                            .first()
                            .content
                            .first()
                            .text

                    val batchResponse =
                        parser.decodeFromString<BatchTranslationResponse>(
                            outputText
                        )

                    if (batchResponse.translations.size != texts.size) {
                        error(
                            "Translation count mismatch: " +
                                    "expected=${texts.size}, " +
                                    "actual=${batchResponse.translations.size}"
                        )
                    }

                    return batchResponse.translations
                }

            } catch (e: SocketTimeoutException) {

                if (attempt >= MAX_RETRIES) {
                    throw e
                }

                println(
                    "Translation request timed out. " +
                            "Retry ${attempt + 1}/$MAX_RETRIES"
                )
            }
        }

        error("Translation failed unexpectedly.")
    }
}