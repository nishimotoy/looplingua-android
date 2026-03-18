package com.looplingua.app.data.pattern

import android.content.Context
import com.looplingua.app.domain.playback.Pattern
import com.looplingua.app.domain.playback.PatternDefinition
import com.looplingua.app.domain.playback.PatternProvider

class AssetPatternProvider(
    private val context: Context
) : PatternProvider {

    override fun get(pattern: Pattern): PatternDefinition {
        return PatternLoader.load(context, "patterns/basic.json")
    }
}