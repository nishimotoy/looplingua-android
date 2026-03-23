package com.looplingua.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SegmentContent(
    original: String,
    translation: String
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = original,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = translation,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Gray
        )
    }
}