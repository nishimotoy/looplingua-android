package com.looplingua.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

        // ===== MODE（主役化） =====
        Surface(
            color = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,   // ← これ追加
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 2.dp,
            onClick = { expanded = true }
        ) {
            Text(
                text = currentPattern.name,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // ===== メニュー =====
        Box {

            Text(
                text = "⋯",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(8.dp)
            )

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
                    text = { Text("Pin") },
                    onClick = { expanded = false }
                )

                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = { expanded = false }
                )
            }
        }
    }
}