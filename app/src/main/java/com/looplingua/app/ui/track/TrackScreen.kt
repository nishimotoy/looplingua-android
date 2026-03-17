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

@Composable
fun TrackScreen(
    controller: PlayerController,
    items: List<TrackListItem>
) {

    val listState = rememberLazyListState()
    val currentIndex by controller.currentIndex.collectAsState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {

        itemsIndexed(items) { _, item ->

            when (item) {

                is TrackListItem.TrackHeader -> {
                    Text(
                        text = "------ ${item.title} ------",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is TrackListItem.SegmentItem -> {

                    val isCurrent = item.segmentIndex == currentIndex

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                controller.playFrom(item.segmentIndex)
                            }
                            .background(
                                if (isCurrent) Color(0xFFE0E0E0) else Color.Transparent
                            )
                            .padding(12.dp)
                    ) {

                        Text(
                            text = if (isCurrent)
                                "▼ ${item.segment.originalText}"
                            else
                                item.segment.originalText,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = item.segment.translationText ?: "",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    // 自動スクロール
    LaunchedEffect(currentIndex) {

        val targetIndex = items.indexOfFirst {
            it is TrackListItem.SegmentItem &&
                    it.segmentIndex == currentIndex
        }

        if (targetIndex >= 0) {
            listState.animateScrollToItem(targetIndex)
        }
    }
}