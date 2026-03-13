package com.looplingua.app.ui.track

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.looplingua.app.domain.model.Segment

@Composable
fun TrackScreen(
    segments: List<Segment>,
    currentIndex: Int,
    onSegmentClick: (Int) -> Unit
) {

    LazyColumn {

        itemsIndexed(segments) { index, segment ->

            Column(
                modifier = Modifier
                    .clickable { onSegmentClick(index) }
                    .padding(16.dp)
            ) {

                val prefix = if (index == currentIndex) "▶ " else "  "

                Text(
                    text = prefix + segment.originalText,
                    fontSize = 20.sp
                )

                Text(
                    text = "  " + segment.translationText,
                    fontSize = 16.sp
                )
            }
        }
    }
}