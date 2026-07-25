package com.looplingua.engine.translation

import kotlinx.serialization.Serializable

@Serializable
data class TranslationResponse(

    val output: List<TranslationOutput>

)

@Serializable
data class TranslationOutput(

    val content: List<TranslationContent>

)

@Serializable
data class TranslationContent(

    val text: String

)