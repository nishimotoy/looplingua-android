package com.looplingua.app.player.track

import com.looplingua.app.domain.model.Track
import com.looplingua.app.playback.StepType
import com.looplingua.app.player.audio.AudioPlayer
import com.looplingua.app.player.resolver.StepResolver

class TrackPlayer(

    private val audioPlayer: AudioPlayer,
    private val resolver: StepResolver

) {

    suspend fun play(track: Track) {

        for (segment in track.segments) {

            val steps = resolver.resolve(track, segment)

            for (step in steps) {

                when (step.type) {

                    StepType.ORIGINAL -> {

                        audioPlayer.playSegment(
                            step.audioRes!!,
                            step.startMs!!,
                            step.endMs!!
                        )

                        audioPlayer.waitUntil(step.endMs)
                    }

                    StepType.PAUSE -> {

                        audioPlayer.pause(step.pauseMs)

                    }

                    else -> {}
                }
            }
        }
    }
}