package com.looplingua.app.ui.track

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.looplingua.app.player.controller.PlayerController
import com.looplingua.app.domain.model.Segment

@Composable
fun TrackScreen(
    controller: PlayerController,
    segments: List<Segment>
) {

    val listState = rememberLazyListState()

    val currentIndex by controller.currentIndex.collectAsState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {

        itemsIndexed(segments) { index, segment ->

            val isCurrent = index == currentIndex

            Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            controller.playFrom(index)
                        }
                        .background(
                            if (isCurrent) Color(0xFFE0E0E0) else Color.Transparent
                        )
                        .padding(12.dp)
                    ) {
                Text(
                    text = if (isCurrent) "▼ ${segment.originalText}" else segment.originalText,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = segment.translationText ?: "",
                    style = MaterialTheme.typography.bodySmall
                )            }
        }
    }

    // 自動スクロール
    LaunchedEffect(currentIndex) {
        listState.animateScrollToItem(currentIndex)
    }
}