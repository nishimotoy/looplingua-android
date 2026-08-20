package com.looplingua.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val currentKey by controller.currentKey.collectAsState()
    val pinnedKey by controller.pinnedKey.collectAsState()
    val playbackSpeed by controller.playbackSpeed.collectAsState()

    val isFlagged = segment?.flagged == true
    val isPinned = pinnedKey != null && pinnedKey == currentKey

    var speedExpanded by remember {
        mutableStateOf(false)
    }

    val playbackSpeeds = listOf(
        0.25f,
        0.5f,
        0.75f,
        1.0f,
        2.0f
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ========================================================
        // Playback Controls
        // ========================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

        // ========================================================
        // Playback Speed / Pin / Flag
        // ========================================================

        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ----------------------------------------------------
            // Playback Speed
            // ----------------------------------------------------

            Box {
                Button(
                    onClick = {
                        speedExpanded = true
                    },
                    colors = colors
                ) {
                    Text("${playbackSpeed}x")
                }

                DropdownMenu(
                    expanded = speedExpanded,
                    onDismissRequest = {
                        speedExpanded = false
                    }
                ) {
                    playbackSpeeds.forEach { speed ->
                        DropdownMenuItem(
                            text = {
                                Text("${speed}x")
                            },
                            onClick = {
                                controller.setPlaybackSpeed(speed)
                                speedExpanded = false
                            }
                        )
                    }
                }
            }

            // ----------------------------------------------------
            // Pin
            // ----------------------------------------------------

            IconButton(
                onClick = {
                    controller.togglePin()
                },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector =
                        if (isPinned) {
                            Icons.Filled.PushPin
                        } else {
                            Icons.Outlined.PushPin
                        },
                    contentDescription =
                        if (isPinned) {
                            "Unpin"
                        } else {
                            "Pin"
                        },
                    modifier = Modifier.size(48.dp),
                    tint =
                        if (isPinned) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                )
            }

            // ----------------------------------------------------
            // Flag
            // ----------------------------------------------------

            IconButton(
                onClick = {
                    controller.toggleFlag()
                },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector =
                        if (isFlagged) {
                            Icons.Filled.Flag
                        } else {
                            Icons.Outlined.Flag
                        },
                    contentDescription =
                        if (isFlagged) {
                            "Unflag"
                        } else {
                            "Flag"
                        },
                    modifier = Modifier.size(48.dp),
                    tint =
                        if (isFlagged) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                )
            }
        }
    }
}