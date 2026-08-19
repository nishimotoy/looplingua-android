package com.looplingua.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.controller.PlayerController

@Composable
fun TopBar(
    controller: PlayerController,
    modifier: Modifier = Modifier
) {

    var patternExpanded by remember {
        mutableStateOf(false)
    }

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    val currentPattern by
    controller.playbackPattern.collectAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.Top
    ) {

        // ========================================================
        // Pattern
        // ========================================================

        Box {

            Surface(
                color =
                    MaterialTheme.colorScheme.primary,
                contentColor =
                    Color.White,
                shape =
                    RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                onClick = {
                    patternExpanded = true
                }
            ) {

                Text(
                    text =
                        " ${currentPattern.name}",
                    modifier =
                        Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                )
            }

            DropdownMenu(
                expanded = patternExpanded,
                onDismissRequest = {
                    patternExpanded = false
                }
            ) {

                Pattern.entries.forEach { pattern ->
                    DropdownMenuItem(
                        text = {
                            Text(pattern.name)
                        },
                        onClick = {
                            controller.setPattern(
                                pattern
                            )
                            patternExpanded = false
                        }
                    )
                }
            }
        }

        // ========================================================
        // Other Menu
        // ========================================================

        Box {

            IconButton(
                onClick = {
                    menuExpanded = true
                }
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MoreVert,
                    contentDescription =
                        "Menu"
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("Edit")
                    },
                    onClick = {
                        menuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            if (controller.isPinned()) {
                                "Unpin"
                            } else {
                                "Pin"
                            }
                        )
                    },
                    onClick = {

                        controller.togglePin()

                        menuExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Settings")
                    },
                    onClick = {
                        menuExpanded = false
                    }
                )
            }
        }
    }
}