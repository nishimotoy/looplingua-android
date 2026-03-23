package com.looplingua.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.looplingua.app.player.controller.PlayerController

@Composable
fun BottomControls(
    controller: PlayerController,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {

    val colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White   // ← 強制
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Button(onClick = { controller.prev() }, colors = colors) {
            Text("PREV")
        }

        Button(onClick = { controller.togglePlay() }, colors = colors) {
            Text(if (isPlaying) "STOP" else "PLAY")
        }

        Button(onClick = { controller.next() }, colors = colors) {
            Text("NEXT")
        }
    }
}