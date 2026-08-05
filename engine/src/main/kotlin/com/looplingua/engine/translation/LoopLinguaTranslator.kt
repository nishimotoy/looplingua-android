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

        val translatedTracks =
            project.tracks.map { track ->

                val translatedSegments =
                    track.segments.map { segment ->

                        println(
                            "Track ${track.trackId} Segment ${segment.segmentId}"
                        )

                        val translated = translator.translate(
                            text = segment.originalAuto,
                            originalLang = track.originalLang,
                            translationLang = track.translationLang
                        )

                        segment.copy(
                            translationAuto = translated
                        )
                    }

                track.copy(
                    segments = translatedSegments
                )
            }

        return project.copy(
            tracks = translatedTracks
        )
    }
}