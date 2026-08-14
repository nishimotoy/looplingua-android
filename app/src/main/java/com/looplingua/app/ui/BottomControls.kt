package com.looplingua.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.looplingua.app.player.controller.PlayerController

@Composable
fun BottomControls(
    controller: PlayerController,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {

    val colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    )

    val segment by controller.currentSegment.collectAsState()
    val isFlagged = segment?.flagged ?: false

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ===== 再生コントロール =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 左エリア
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { controller.prev() },
                    colors = colors
                ) {
                    Text("PREV")
                }
            }

            // 中央エリア
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { controller.togglePlay() },
                    colors = colors,
                    modifier = Modifier.width(140.dp)
                ) {
                    Text(if (isPlaying) "STOP" else "PLAY")
                }
            }

            // 右エリア
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { controller.next() },
                    colors = colors
                ) {
                    Text("NEXT")
                }
            }
        }

        // ===== フラグ =====
        Button(
            onClick = { controller.toggleFlag() },
            colors = colors,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                if (isFlagged) {
                    "FLAGGED"
                } else {
                    "FLAG"
                }
            )
        }
    }
}