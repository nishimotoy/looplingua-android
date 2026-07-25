package com.looplingua.engine.converter

import com.looplingua.engine.model.LoopLinguaProject
import com.looplingua.engine.model.LoopLinguaSegment
import com.looplingua.engine.model.LoopLinguaTrack
import com.looplingua.engine.whisper.WhisperResponse
import java.io.File

/**
 * Whisper API のレスポンスを
 * LoopLingua 内部データへ変換するクラス。
 *
 * 役割は「変換だけ」。
 * ファイル保存や翻訳などは行わない。
 */
class WhisperProjectConverter {

    /**
     * WhisperResponse → LoopLinguaProject
     *
     * @param response Whisper API が返した文字起こし結果
     * @param audioPath 元音声ファイルのパス
     */
    fun convert(
        response: WhisperResponse,
        audioPath: String
    ): LoopLinguaProject {

        //------------------------------------------------------------------
        // ① Whisper の各 Segment を LoopLinguaSegment に変換する
        //------------------------------------------------------------------

        val segments = response.segments.mapIndexed { index, segment ->

            LoopLinguaSegment(

                // LoopLingua 内で管理する連番ID
                // （Whisper の id は使わない）
                segmentId = index,

                // Whisper は秒(Double)で返すので、
                // LoopLingua のミリ秒(Long)へ変換する
                originalStartMs = (segment.start * 1000).toLong(),
                originalEndMs = (segment.end * 1000).toLong(),

                // Whisper が認識した文字列
                // 編集前のオリジナルとして保存する
                originalAuto = segment.text,

                // ユーザー編集前なので空
                originalUser = "",

                // 翻訳はまだ行っていない
                translationAuto = "",

                // ユーザー翻訳もまだない
                translationUser = "",

                // メモもまだない
                memoUser = ""
            )
        }

        //------------------------------------------------------------------
        // ② Track を作成する
        //------------------------------------------------------------------

        val track = LoopLinguaTrack(

            // 現在は1ファイルだけなので0固定
            // 将来は複数Track対応予定
            trackId = 0,

            // フルパスではなくファイル名だけ保存
            fileName = File(audioPath).name,

            // Whisper が認識した言語
            sourceLanguage = response.language,

            // 翻訳先言語はまだ未設定
            targetLanguage = "Japanese",

            // ①で作ったSegment一覧
            segments = segments
        )

        //------------------------------------------------------------------
        // ③ Project を作成して返す
        //------------------------------------------------------------------

        return LoopLinguaProject(

            // Project作成時に設定予定
            projectId = "",

            // 拡張子を除いたファイル名を仮のプロジェクト名にする
            projectName = File(audioPath).nameWithoutExtension,

            // 現在はTrackは1つだけ
            tracks = listOf(track)
        )
    }
}