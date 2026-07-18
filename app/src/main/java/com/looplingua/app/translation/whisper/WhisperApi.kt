package com.looplingua.app.translation.whisper

import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody

class WhisperApi(
    private val apiKey: String
) {

    private val client = OkHttpClient()

    fun transcribe(
        inputMp3: File,
        outputJson: File
    ): WhisperResponse {

        // multipart
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                inputMp3.name,
                inputMp3.asRequestBody("audio/mpeg".toMediaType())
            )
            .addFormDataPart(
                "model",
                "gpt-4o-transcribe"
            )
            .addFormDataPart(
                "response_format",
                "verbose_json"
            )
            .build()

        // HTTP Request
        val request = Request.Builder()
            .url("https://api.openai.com/v1/audio/transcriptions")
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        // send
        client.newCall(request).execute().use { response ->

            if (!response.isSuccessful) {
                error(response.body?.string() ?: "Unknown error")
            }

            val json = response.body!!.string()

            outputJson.writeText(json)

            return Json.decodeFromString<WhisperResponse>(json)

        }
    }
}