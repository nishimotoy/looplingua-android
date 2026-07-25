package com.looplingua.engine.translation

import com.looplingua.engine.model.LoopLinguaProject

class LoopLinguaTranslator(

    private val translator: Translator

) {

    fun translate(
        project: LoopLinguaProject
    ): LoopLinguaProject {

        val track = project.tracks.first()
        val segment = track.segments.first()
        println(segment.originalAuto)

        val translated = translator.translate(
            text = segment.originalAuto,
            sourceLanguage = track.sourceLanguage,
            targetLanguage = track.targetLanguage
        )

        println(translated)

        val translatedSegment = segment.copy(
            translationAuto = translated
        )

        println(translatedSegment.translationAuto)

        return project
    }
}