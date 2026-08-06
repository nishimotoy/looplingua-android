package com.looplingua.engine.translation

interface BatchTranslator {

    fun translate(
        texts: List<String>,
        originalLang: String,
        translationLang: String
    ): List<String>
}