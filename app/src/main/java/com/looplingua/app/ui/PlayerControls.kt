package com.looplingua.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.looplingua.app.player.controller.PlayerController

@Composable
fun PlayerControls(
    controller: PlayerController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Button(onClick = {
            controller.prev()
        }) {
            Text("PREV")
        }

        Button(onClick = {
            controller.togglePlay()
        }) {
            Text("PLAY / STOP")
        }

        Button(onClick = {
            controller.next()
        }) {
            Text("NEXT")
        }
    }
}