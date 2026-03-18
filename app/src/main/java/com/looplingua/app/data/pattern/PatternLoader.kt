package com.looplingua.app.data.pattern

import android.content.Context
import com.looplingua.app.domain.playback.PatternDefinition
import com.looplingua.app.domain.playback.StepType
import org.json.JSONObject

object PatternLoader {

    fun load(context: Context, fileName: String): PatternDefinition {
        val jsonString = context.assets.open(fileName)
            .bufferedReader()
            .use { it.readText() }

        val json = JSONObject(jsonString)

        val name = json.getString("name")
        val stepsJson = json.getJSONArray("steps")

        val steps = mutableListOf<StepType>()
        for (i in 0 until stepsJson.length()) {
            steps.add(StepType.valueOf(stepsJson.getString(i)))
        }

        return PatternDefinition(name, steps)
    }
}