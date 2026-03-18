package com.looplingua.app.domain.playback

interface PatternProvider {
    fun get(pattern: Pattern): PatternDefinition
}