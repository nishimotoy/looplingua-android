package com.looplingua.app

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.player.audio.AudioPlayer
import com.looplingua.app.player.resolver.StepResolver
import com.looplingua.app.player.track.TrackPlayer
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var audioPlayer: AudioPlayer
    private lateinit var trackPlayer: TrackPlayer

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // AudioPlayer (ExoPlayer wrapper)
        audioPlayer = AudioPlayer(this)

        // StepResolver
        val resolver = StepResolver()

        // TrackPlayer
        trackPlayer = TrackPlayer(
            audioPlayer,
            resolver
        )

        // テスト用トラック作成
        val track = createTestTrack()

        val playButton = findViewById<Button>(R.id.playButton)

        playButton.setOnClickListener {

            lifecycleScope.launch {

                trackPlayer.play(track)

            }
        }
    }

    /**
     * greetings_uk.mp3 のタイムコードに合わせた
     * テスト用 Segment データ
     */
    private fun createTestTrack(): Track {

        val segments = listOf(

            Segment(
                id = "1",
                startMs = 0,
                endMs = 2000,
                translation = "Good afternoon"
            ),

            Segment(
                id = "2",
                startMs = 2000,
                endMs = 5000,
                translation = "Good morning"
            ),

            Segment(
                id = "3",
                startMs = 5000,
                endMs = 7000,
                translation = "Good evening"
            ),

            Segment(
                id = "4",
                startMs = 7000,
                endMs = 10000,
                translation = "Goodbye"
            ),

            Segment(
                id = "5",
                startMs = 10000,
                endMs = 14000,
                translation = "Good night"
            )
        )

        return Track(
            id = "greetings",
            title = "Greetings",
            audioRes = R.raw.greetings_uk,
            segments = segments
        )
    }

    override fun onDestroy() {

        super.onDestroy()

        audioPlayer.release()

    }
}