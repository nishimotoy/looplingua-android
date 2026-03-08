package com.looplingua.app.playback

data class PlaybackStep(

    val type: StepType,

    val audioRes: Int? = null,

    val startMs: Int? = null,

    val endMs: Int? = null,

    val text: String? = null,

    val pauseMs: Long = 0
)