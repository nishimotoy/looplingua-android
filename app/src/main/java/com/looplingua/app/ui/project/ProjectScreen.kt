package com.looplingua.app.ui.project

import com.looplingua.app.ui.theme.TealPrimary
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.looplingua.app.domain.model.TrackWithSegments

@Composable
fun ProjectScreen(
    projects: List<ProjectItem>,
    tracksByProject: Map<String, List<TrackWithSegments>>,
    onProjectPlay: (ProjectItem) -> Unit,
    onTrackSelected: (ProjectItem, TrackWithSegments) -> Unit,
    onCancel: () -> Unit
) {
    var expandedProjectId by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .clickable {
                        onCancel()
                    }
                    .padding(16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(projects) { project ->

                val isExpanded =
                    expandedProjectId == project.projectId

                val tracks =
                    tracksByProject[project.projectId]
                        ?: emptyList()

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = if (isExpanded) {
                                "▼"
                            } else {
                                "▶"
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            color = TealPrimary,
                            modifier = Modifier
                                .clickable {
                                    onProjectPlay(project)
                                }
                                .padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    bottom = 16.dp
                                )
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    expandedProjectId =
                                        if (isExpanded) {
                                            null
                                        } else {
                                            project.projectId
                                        }
                                }
                                .padding(
                                    start = 8.dp,
                                    top = 16.dp,
                                    bottom = 16.dp
                                )
                        ) {

                            Text(
                                text = project.projectName,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = project.projectId,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }
                    }

                    if (isExpanded) {

                        tracks.forEach { track ->

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onTrackSelected(
                                            project,
                                            track
                                        )
                                    }
                                    .padding(
                                        start = 48.dp,
                                        top = 12.dp,
                                        bottom = 12.dp,
                                        end = 16.dp
                                    )
                            ) {

                                Text(
                                    text = track.track.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}