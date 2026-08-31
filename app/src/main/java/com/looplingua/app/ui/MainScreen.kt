package com.looplingua.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.looplingua.app.player.controller.PlayerController
import com.looplingua.app.ui.project.ProjectItem
import com.looplingua.app.ui.project.ProjectScreen
import com.looplingua.app.ui.track.TrackScreen

@Composable
fun MainScreen(
    controller: PlayerController,
    projects: List<ProjectItem>,
    onProjectSelected: (ProjectItem) -> Unit
) {

    var showProjectScreen by remember {
        mutableStateOf(false)
    }

    if (showProjectScreen) {

        ProjectScreen(
            projects = projects,
            onProjectSelected = {
                showProjectScreen = false
                onProjectSelected(it)
            },
            onCancel = {
                showProjectScreen = false
            }
        )

    } else {

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
                SegmentScreen(
                    controller = controller,
                    onProjectSelected = {
                        showProjectScreen = true
                    }
                )
            }

            // 下
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            ) {
                TrackScreen(
                    controller = controller
                )
            }
        }
    }
}