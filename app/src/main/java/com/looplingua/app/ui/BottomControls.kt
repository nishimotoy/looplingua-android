package com.looplingua.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.looplingua.app.player.controller.PlayerController

@Composable
fun BottomControls(
    controller: PlayerController,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // PREV（サブ）
        OutlinedButton(
            onClick = { controller.prev() }
        ) {
            Text("PREV")
        }

        // PLAY / STOP（メイン）
        Button(
            onClick = { controller.togglePlay() },
            modifier = Modifier.height(56.dp)
        ) {
            Text(
                text = if (isPlaying) "STOP" else "PLAY",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // NEXT（サブ）
        OutlinedButton(
            onClick = { controller.next() }
        ) {
            Text("NEXT")
        }
    }
}