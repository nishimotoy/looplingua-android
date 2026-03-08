package com.looplingua.app.domain.playback

sealed class AudioSource {
    data class Resource(val resId: Int) : AudioSource()
    data class File(val filePath: String) : AudioSource()
}