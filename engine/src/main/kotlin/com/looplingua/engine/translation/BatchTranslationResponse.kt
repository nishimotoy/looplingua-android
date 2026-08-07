package com.looplingua.engine.translation

import kotlinx.serialization.Serializable

@Serializable
data class BatchTranslationResponse(
    val translations: List<String>
)