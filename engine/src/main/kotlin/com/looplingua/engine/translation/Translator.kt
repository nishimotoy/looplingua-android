package com.looplingua.engine.translation

import kotlinx.serialization.Serializable

interface Translator {

    fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): String
}
