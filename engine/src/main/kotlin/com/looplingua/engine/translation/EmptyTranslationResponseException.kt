package com.looplingua.engine.translation

class EmptyTranslationResponseException :
    IllegalStateException(
        "OpenAI returned an empty translation response."
    )