package com.looplingua.app

import android.util.Log
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.looplingua.app.service.playback.SimpleSegmentPlayer

class MainActivity : AppCompatActivity() {

    private lateinit var segmentPlayer: SimpleSegmentPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TEST", "MainActivity started")

        val button = Button(this).apply {
            text = "Play Segment 1"
        }

        setContentView(button)

        segmentPlayer = SimpleSegmentPlayer(this)

        button.setOnClickListener {
            segmentPlayer.playSegment(
                resId = R.raw.greetings_uk,
                startMs = 0,
                endMs = 2000
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        segmentPlayer.release()
    }
}
