package com.looplingua.engine.translation

import com.looplingua.engine.model.LoopLinguaProject

class LoopLinguaTranslator(

    private val translator: Translator

) {

    fun translate(
        project: LoopLinguaProject
    ): LoopLinguaProject {

        val track = project.tracks.first()
        val translatedSegments =
            track.segments.map { segment ->

                println("Segment ${segment.segmentId}")

                val translated = translator.translate(
                    text = segment.originalAuto,
                    sourceLanguage = track.sourceLanguage,
                    targetLanguage = track.targetLanguage
                    //  println(requestJson) in OpenAiTranslator
                )

                println(translated)

                segment.copy(
                    translationAuto = translated
                )
            }

        println(translatedSegments.size)
        println(translatedSegments.first().translationAuto)

        return project
    }
}