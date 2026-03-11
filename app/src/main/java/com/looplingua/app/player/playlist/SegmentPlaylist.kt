package com.looplingua.app.player.playlist

import com.looplingua.app.domain.model.Segment

class SegmentPlaylist(
    private val segments: List<Segment>
) {

    private var currentSegmentIndex: Int = 0

    /**
     * 現在のセグメントを取得
     */
    fun currentSegment(): Segment? {
        if (segments.isEmpty()) return null
        return segments.getOrNull(currentSegmentIndex)
    }

    /**
     * 次のセグメントへ進む
     */
    fun nextSegment(): Segment? {
        if (!hasNext()) return null

        currentSegmentIndex++
        return segments.getOrNull(currentSegmentIndex)
    }

    /**
     * 次のセグメントが存在するか
     */
    fun hasNext(): Boolean {
        return currentSegmentIndex < segments.size - 1
    }

    /**
     * 先頭に戻る
     */
    fun reset() {
        currentSegmentIndex = 0
    }

    /**
     * 現在のインデックス取得
     */
    fun getCurrentIndex(): Int {
        return currentSegmentIndex
    }

    /**
     * 総セグメント数
     */
    fun size(): Int {
        return segments.size
    }

    /**
     * 任意のセグメントへジャンプ
     */
    fun seekTo(index: Int): Segment? {
        if (index < 0 || index >= segments.size) return null

        currentSegmentIndex = index
        return segments[currentSegmentIndex]
    }
}