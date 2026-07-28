package com.looplingua.engine.translation

import com.looplingua.engine.model.LoopLinguaProject

// Project全体を翻訳する。
// 各Segmentの翻訳自体は Translator に委譲する。

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
                )

                segment.copy(
                    translationAuto = translated
                )
            }

        val translatedTrack = track.copy(
            segments = translatedSegments
        )

        val translatedProject = project.copy(
            tracks = listOf(translatedTrack)
        )

        return translatedProject
    }
}