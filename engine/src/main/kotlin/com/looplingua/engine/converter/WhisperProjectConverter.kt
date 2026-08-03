package com.looplingua.engine.converter

import com.looplingua.engine.model.LoopLinguaSegment
import com.looplingua.engine.model.LoopLinguaTrack
import com.looplingua.engine.whisper.WhisperResponse
import java.io.File

/**
 * Whisper API のレスポンスを
 * LoopLingua の Track データへ変換するクラス。
 *
 * 役割は「変換だけ」。
 * Project の生成、ファイル保存、翻訳などは行わない。
 */
class WhisperProjectConverter {

    /**
     * WhisperResponse → LoopLinguaTrack
     *
     * @param response Whisper API が返した文字起こし結果
     * @param audioPath 元音声ファイルのパス
     * @param trackId Project 内での Track ID
     * @param translationLang 翻訳言語
     */
    fun convert(
        response: WhisperResponse,
        audioPath: String,
        trackId: Int,
        translationLang: String
    ): LoopLinguaTrack {

        val segments = response.segments.mapIndexed { index, segment ->

            LoopLinguaSegment(
                segmentId = index,

                originalStartMs = (segment.start * 1000).toLong(),
                originalEndMs = (segment.end * 1000).toLong(),

                originalAuto = segment.text,
                originalUser = "",

                translationAuto = "",
                translationUser = "",

                memoUser = ""
            )
        }

        return LoopLinguaTrack(
            trackId = trackId,
            fileName = File(audioPath).name,
            originalLang = response.language,
            translationLang = translationLang,
            segments = segments
        )
    }
}