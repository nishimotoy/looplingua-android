package com.looplingua.app.service.playback

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player
import android.util.Log
import com.looplingua.app.domain.playback.PlaybackStep

class SimpleSegmentPlayer(
    private val context: Context
) {

    private val player = ExoPlayer.Builder(context).build()
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false
    private var steps: List<PlaybackStep> = emptyList()
    private var currentStepIndex = 0
    fun playSegment(
        @RawRes resId: Int,
        startMs: Long,
        endMs: Long
    ) {
        val uri = "android.resource://${context.packageName}/$resId"
        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)
        player.prepare()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    player.seekTo(startMs)
                    player.play()
                    startPositionMonitoring(endMs) {
                        currentStepIndex++
                        executeNextStep()
                    }
                    player.removeListener(this)
                }
            }
        })
    }

    private var monitorRunnable: Runnable? = null

    private fun startPositionMonitoring(
        endMs: Long,
        onFinished: () -> Unit
    ) {
        monitorRunnable?.let { handler.removeCallbacks(it) }

        monitorRunnable = object : Runnable {
            override fun run() {
                if (player.currentPosition >= endMs) {
                    player.pause()
                    onFinished()
                } else {
                    handler.postDelayed(this, 50)
                }
            }
        }

        handler.post(monitorRunnable!!)
    }
    fun release() {
        player.release()
        monitorRunnable?.let { handler.removeCallbacks(it) }
    }

    fun playSteps(
        @RawRes resId: Int,
        stepList: List<PlaybackStep>
    ) {
        steps = stepList
        currentStepIndex = 0

        val uri = "android.resource://${context.packageName}/$resId"
        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)
        player.prepare()

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    executeNextStep()
                    player.removeListener(this)
                }
            }
        })
    }

    private fun executeNextStep() {

        Log.d("PLAYER", "Step: $currentStepIndex -> ${steps[currentStepIndex]}")

        if (currentStepIndex >= steps.size) {
            return
        }

        val step = steps[currentStepIndex]

        when (step) {

            is PlaybackStep.PlayOriginal -> {
                playSegmentInternal(
                    step.segment.startTimeMs,
                    step.segment.endTimeMs
                )
            }

            is PlaybackStep.PlayTranslation -> {
                playSegmentInternal(
                    step.segment.startTimeMs,
                    step.segment.endTimeMs
                )
            }

            is PlaybackStep.Pause -> {
                handler.postDelayed({
                    currentStepIndex++
                    executeNextStep()
                }, step.durationMs)
            }
        }
    }
    private fun playSegmentInternal(startMs: Long, endMs: Long) {

        player.seekTo(startMs)
        player.play()

        startPositionMonitoring(endMs) {
            currentStepIndex++
            executeNextStep()
        }
    }

}
