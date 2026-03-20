package com.looplingua.app.ui.track

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.rememberLazyListState
import com.looplingua.app.player.controller.PlayerController

@Composable
fun TrackScreen(
    controller: PlayerController,
    items: List<TrackListItem>
) {

    val currentSegment by controller.currentSegment.collectAsState()
    val currentTrack by controller.currentTrack.collectAsState()

    val listState = rememberLazyListState()

    //  現在位置（trackId + segmentIdで特定）
    val currentIndex = remember(currentSegment, currentTrack, items) {
        items.indexOfFirst {
            it is TrackListItem.SegmentItem &&
                    it.trackId == currentTrack?.track?.id &&
                    it.segment.id == currentSegment?.id
        }
    }

    //  自動スクロール
    LaunchedEffect(currentIndex) {
        if (currentIndex >= 0) {
            listState.animateScrollToItem(currentIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {

        itemsIndexed(items) { _, item ->

            when (item) {

                is TrackListItem.TrackHeader -> {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp)
                    )
                }

                is TrackListItem.SegmentItem -> {

                    val isCurrent =
                        item.trackId == currentTrack?.track?.id &&
                                item.segment.id == currentSegment?.id

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isCurrent) Color(0xFFBBDEFB)
                                else Color.Transparent
                            )
                            .clickable {
                                controller.playFrom(item.segment)
                            }
                            .padding(12.dp)
                    ) {

                        Text(
                            text = item.segment.originalText,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.segment.translationText ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}