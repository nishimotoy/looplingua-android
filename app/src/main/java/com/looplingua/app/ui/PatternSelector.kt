package com.looplingua.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.looplingua.app.domain.playback.Pattern

@Composable
fun PatternSelector(
    current: Pattern,
    onChange: (Pattern) -> Unit,
    modifier: Modifier = Modifier   // ← 追加
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Button(onClick = { expanded = true }) {
            Text("再生パターン: ${current.name}")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Pattern.entries.forEach { pattern ->
                DropdownMenuItem(
                    text = { Text(pattern.name) },
                    onClick = {
                        onChange(pattern)
                        expanded = false
                    }
                )
            }
        }
    }
}