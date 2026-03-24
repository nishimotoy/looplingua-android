package com.looplingua.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    var expanded by remember { mutableStateOf(false) }
    var currentPattern by remember { mutableStateOf(Pattern.BASIC) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            color = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 2.dp,
            onClick = { expanded = true }
        ) {
            Text(
                text = " ${currentPattern.name}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                Pattern.entries.forEach { pattern ->
                    DropdownMenuItem(
                        text = { Text(pattern.name) },
                        onClick = {
                            currentPattern = pattern
                            controller.setPattern(pattern)
                            expanded = false
                        }
                    )
                }

                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = { expanded = false }
                )

                DropdownMenuItem(
                    text = { Text(if (controller.isPinned()) "Unpin" else "Pin") },
                    onClick = {
                        controller.togglePin()
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = { expanded = false }
                )
            }
        }
    }
}