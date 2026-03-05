package com.looplingua.app

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.looplingua.app.service.playback.PlayerStep
import com.looplingua.app.service.playback.SimpleSegmentPlayer
import com.looplingua.app.service.playback.StepResolver

class MainActivity : AppCompatActivity() {

    private lateinit var segmentPlayer: SimpleSegmentPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        segmentPlayer = SimpleSegmentPlayer(this)

        val btnPlay = findViewById<Button>(R.id.btnPlay)

        btnPlay.setOnClickListener {

            // サンプルトラック取得（SampleTracks などでも可）
            val track = SampleTracks.greetings() // Segment のリストを持つ Track

            // StepResolver で PlayerStep を生成
            val resolver = StepResolver()
            val steps: List<PlayerStep> = resolver.buildSteps(track, null, null)

            // 再生
            segmentPlayer.playSteps(steps)
        }
    }
}