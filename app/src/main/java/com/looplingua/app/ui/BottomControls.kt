package com.looplingua.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "◀",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.clickable {
                controller.prev()
            }
        )

        Text(
            text = if (isPlaying) "■" else "▶",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.clickable {
                controller.togglePlay()
            }
        )

        Text(
            text = "▶",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.clickable {
                controller.next()
            }
        )
    }
}