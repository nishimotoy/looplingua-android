package com.looplingua.app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.playback.PlaybackStep
import com.looplingua.app.service.playback.SimpleSegmentPlayer

class MainActivity : AppCompatActivity() {

    private lateinit var segmentPlayer: SimpleSegmentPlayer

    private lateinit var textOriginal: TextView
    private lateinit var textTranslation: TextView
    private lateinit var btnPlay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textOriginal = findViewById(R.id.textOriginal)
        textTranslation = findViewById(R.id.textTranslation)
        btnPlay = findViewById(R.id.btnPlay)

        segmentPlayer = SimpleSegmentPlayer(this)
        segmentPlayer.onSegmentChanged = { segment ->
            runOnUiThread {
                textOriginal.text = segment.originalText
                textTranslation.text = segment.translationText
            }
        }

        btnPlay.setOnClickListener {

            val segment1 = Segment(
                id = "1",
                startTimeMs = 0,
                endTimeMs = 2000,
                sourceMediaId = "",
                originalText = "Добрий день",
                translationText = "Hello"
            )

            val segment2 = Segment(
                id = "2",
                startTimeMs = 2000,
                endTimeMs = 4000,
                sourceMediaId = "",
                originalText = "Доброго ранку",
                translationText = "Good morning"
            )

            val steps = listOf(
                PlaybackStep.PlayOriginal(segment1, 0),
                PlaybackStep.Pause(500),
                PlaybackStep.PlayTranslation(segment1),
                PlaybackStep.Pause(500),
                PlaybackStep.PlayOriginal(segment1, 1),

                PlaybackStep.PlayOriginal(segment2, 0),
                PlaybackStep.Pause(500),
                PlaybackStep.PlayTranslation(segment2),
                PlaybackStep.Pause(500),
                PlaybackStep.PlayOriginal(segment2, 1),
            )

            // 表示更新（最初の文だけ表示例）
            textOriginal.text = segment1.originalText
            textTranslation.text = segment1.translationText

            segmentPlayer.playSteps(
                R.raw.greetings_uk,
                steps
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        segmentPlayer.release()
    }
}