package com.looplingua.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.player.controller.PlayerController
import com.looplingua.app.ui.track.TrackScreen

@Composable
fun MainScreen(
    controller: PlayerController,
    segments: List<Segment>
) {

    val currentSegment = controller.currentSegment.collectAsState().value

    val currentIndex = segments.indexOfFirst {
        it.id == currentSegment?.id
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        // 上：再生画面
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        ) {
            SegmentScreen(controller = controller)
        }

        // 下：トラックリスト
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        ) {
            TrackScreen(
                segments = segments,
                currentIndex = currentIndex,
                onSegmentClick = { index ->
                    controller.playFrom(index)
                }
            )
        }
    }
}