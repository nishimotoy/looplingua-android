package com.looplingua.engine.translation

import com.looplingua.engine.model.LoopLinguaProject

// Project全体を翻訳する。
// 各TrackのSegmentをBatchに分け、
// BatchTranslatorに翻訳を委譲する。

class LoopLinguaTranslator(

    private val translator: BatchTranslator

) {

    companion object {
        private const val MAX_SEGMENTS_PER_BATCH = 20
    }

    fun translate(
        project: LoopLinguaProject
    ): LoopLinguaProject {

        val translatedTracks =
            project.tracks.map { track ->

                val translatedSegments =
                    track.segments
                        .chunked(MAX_SEGMENTS_PER_BATCH)  // chunked： listをbatchにする
                        .flatMap { batch ->                     // flatMap： batchごとに繰り返し処理

                            val texts =
                                batch.map { segment ->
                                    segment.originalAuto
                                }

                            println(
                                "Track ${track.trackId}: " +
                                        "translating ${batch.size} segments"
                            )

                            val translations =
                                translator.translate(
                                    texts = texts,
                                    originalLang = track.originalLang,
                                    translationLang = track.translationLang
                                )

                            if (translations.size != batch.size) {
                                throw IllegalStateException(
                                    "Translation count mismatch: " +
                                            "input=${batch.size}, " +
                                            "output=${translations.size}"
                                )
                            }

                            batch.mapIndexed { index, segment ->
                                segment.copy(
                                    translationAuto =
                                        translations[index]
                                )
                            }
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