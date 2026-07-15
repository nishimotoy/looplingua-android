package com.looplingua.app.translation.whisper

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class WhisperApi(
    private val apiKey: String
) {

    private val client = OkHttpClient()

    suspend fun transcribe(
        inputMp3: File
    ): String {

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

            val json = response.body?.string()
                ?: error("Response body is null")

            return json

        }
    }
}