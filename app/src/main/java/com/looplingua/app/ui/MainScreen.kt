package com.looplingua.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.looplingua.app.player.controller.PlayerController
import com.looplingua.app.ui.track.TrackListItem
import com.looplingua.app.ui.track.TrackScreen

@Composable
fun MainScreen(
    controller: PlayerController,
    items: List<TrackListItem>   // ← ここ変更
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        // 上
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
        ) {
            SegmentScreen(controller = controller)
        }

        // 下
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
        ) {
            TrackScreen(
                controller = controller,
                items = items   // ← ここ変更
            )
        }
    }
}