package com.looplingua.app.data

import android.content.Context
import com.google.gson.Gson
import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track

data class SegmentFile(
    val track: Track,
    val segments: List<Segment>
)

object SegmentFileLoader {

    fun loadFromAssets(
        context: Context,
        fileName: String
    ): SegmentFile {

        val json = context.assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }

        return Gson().fromJson(json, SegmentFile::class.java)
    }
}