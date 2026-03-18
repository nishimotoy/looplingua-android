package com.looplingua.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.player.controller.PlayerController
import com.looplingua.app.ui.track.TrackListItem
import com.looplingua.app.ui.track.TrackScreen

@Composable
fun MainScreen(
    controller: PlayerController,
    items: List<TrackListItem>   // ← ここ変更
) {

    var currentPattern by remember { mutableStateOf(Pattern.BASIC) }

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

            // 右上に配置
            PatternSelector(
                current = currentPattern,
                onChange = {
                    currentPattern = it          // ← UI更新
                    controller.setPattern(it)    // ← 再生変更
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }


        // 中
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            // PlayerControls(controller = controller)
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