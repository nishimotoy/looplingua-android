package com.looplingua.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.looplingua.app.player.controller.PlayerController

@Composable
fun SegmentScreen(controller: PlayerController) {

    val segment by controller.currentSegment.collectAsState()
    val isPlaying by controller.isPlaying.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ===== 中央コンテンツ =====
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SegmentContent(
                original = segment?.originalText ?: "",
                translation = segment?.translationText ?: ""
            )
        }

        // ===== 上部バー =====
        TopBar(
            controller = controller,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // ===== 下部コントロール =====
        BottomControls(
            controller = controller,
            isPlaying = isPlaying,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}