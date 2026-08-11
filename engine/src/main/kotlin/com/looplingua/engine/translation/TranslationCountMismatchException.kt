package com.looplingua.engine.translation

class TranslationCountMismatchException(
    val inputTexts: List<String>,
    val outputTranslations: List<String>
) : IllegalStateException(
    "Translation count mismatch: " +
            "expected=${inputTexts.size}, " +
            "actual=${outputTranslations.size}"
)