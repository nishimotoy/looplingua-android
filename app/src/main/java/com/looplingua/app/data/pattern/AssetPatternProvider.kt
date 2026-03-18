package com.looplingua.app.data.pattern

import android.content.Context
import android.util.Log
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.domain.playback.PatternDefinition
import com.looplingua.app.domain.playback.PatternProvider

class AssetPatternProvider(
    private val context: Context
) : PatternProvider {

    override fun get(pattern: Pattern): PatternDefinition {
        val fileName = when (pattern) {
            Pattern.BASIC -> "patterns/basic.json"
            Pattern.SHADOWING -> "patterns/shadowing.json"
            Pattern.ORIGINAL_ONLY -> "patterns/original_only.json"
        }
        Log.d("PATTERN", "Loading pattern: $fileName")
        return PatternLoader.load(context, fileName)
    }
}