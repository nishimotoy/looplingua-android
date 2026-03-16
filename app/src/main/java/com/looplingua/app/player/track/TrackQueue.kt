package com.looplingua.app.player.track

import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.model.Segment

class TrackQueue(
    private val trackPlayer: TrackPlayer
) {

    private val tracks = mutableListOf<Pair<Track, List<Segment>>>()
    private var currentIndex = 0

    fun addTrack(track: Track, segments: List<Segment>) {
        tracks.add(track to segments)
    }

    fun start() {
        playNextTrack()
    }

    private fun playNextTrack() {

        if (currentIndex >= tracks.size) return

        val (track, segments) = tracks[currentIndex]

        trackPlayer.load(track, segments)

        trackPlayer.start {
            currentIndex++
            playNextTrack()
        }
    }

    fun stop() {
        trackPlayer.stop()
        currentIndex = 0
    }
}