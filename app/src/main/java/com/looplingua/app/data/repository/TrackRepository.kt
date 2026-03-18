package com.looplingua.app.data.repository

import android.content.Context
import com.looplingua.app.data.SegmentFile
import com.looplingua.app.data.SegmentFileLoader

class TrackRepository(
    private val context: Context
) {

    fun loadInitialTracks(): List<SegmentFile> {
        return listOf(
            SegmentFileLoader.loadFromAssets(context, "testdata/tracks/1/track.json"),
            SegmentFileLoader.loadFromAssets(context, "testdata/tracks/2/track.json")
        )
    }
}