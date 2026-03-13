package com.looplingua.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.looplingua.app.player.controller.PlayerController

@Composable
fun SegmentScreen(
    controller: PlayerController
) {
    val segment by controller.currentSegment.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(80.dp))

        Text(
            text = segment?.originalText ?: "",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = segment?.translationText ?: "",
            fontSize = 22.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = segment?.memoText ?: "",
            fontSize = 18.sp,
            color = Color.DarkGray
        )

        PlayerControls(controller)
    }
}