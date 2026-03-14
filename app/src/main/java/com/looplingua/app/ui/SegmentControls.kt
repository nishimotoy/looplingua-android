package com.looplingua.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.looplingua.app.player.controller.PlayerController

@Composable
fun SegmentControls(controller: PlayerController) {

    val isPlaying by controller.isPlaying.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Button(onClick = { controller.prev() }) {
            Text("PREV")
        }

        Button(onClick = { controller.togglePlay() }) {
            Text(if (isPlaying) "STOP ■" else "PLAY ▶")
        }

        Button(onClick = { controller.next() }) {
            Text("NEXT")
        }
    }
}