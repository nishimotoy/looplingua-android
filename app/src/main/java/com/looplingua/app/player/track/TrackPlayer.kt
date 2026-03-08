package com.example.audio.engine

import com.example.audio.model.Track
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrackPlayer(
    private val stepResolver: StepResolver,
    private val audioPlayer: AudioPlayer
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var job: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    /**
     * Track 再生開始
     */
    fun play(track: Track) {
        stop()

        job = scope.launch {

            _isPlaying.value = true

            val steps = stepResolver.resolve(track)

            for (step in steps) {

                ensureActive()

                when (step.type) {

                    StepType.PLAY -> {
                        audioPlayer.play(step.source)
                        audioPlayer.awaitCompletion()
                    }

                    StepType.SILENCE -> {
                        delay(step.durationMs)
                    }
                }
            }

            _isPlaying.value = false
        }
    }

    /**
     * 停止
     */
    fun stop() {
        job?.cancel()
        job = null
        audioPlayer.stop()
        _isPlaying.value = false
    }

    /**
     * 一時停止
     */
    fun pause() {
        audioPlayer.pause()
        _isPlaying.value = false
    }

    /**
     * 再開
     */
    fun resume() {
        audioPlayer.resume()
        _isPlaying.value = true
    }
}