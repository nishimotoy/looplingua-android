package com.looplingua.app.player.factory

import android.content.Context
import com.looplingua.app.data.pattern.AssetPatternProvider
import com.looplingua.app.domain.model.Track
import com.looplingua.app.player.audio.AudioPlayer
import com.looplingua.app.player.controller.PlayerController
import com.looplingua.app.player.queue.SegmentQueue
import com.looplingua.app.player.segment.SegmentPlayer
import com.looplingua.app.player.sequence.SequenceBuilder

object PlayerFactory {

    fun create(
        context: Context,
        track: Track
    ): PlayerController {

        val audioPlayer = AudioPlayer(context)
        val segmentPlayer = SegmentPlayer(audioPlayer)
        val queue = SegmentQueue()
        val provider = AssetPatternProvider(context)
        val sequenceBuilder = SequenceBuilder(provider)

        return PlayerController(
            track = track,
            queue = queue,
            sequenceBuilder = sequenceBuilder,
            segmentPlayer = segmentPlayer
        )
    }
}