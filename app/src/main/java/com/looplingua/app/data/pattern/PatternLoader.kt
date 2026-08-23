package com.looplingua.app.data.pattern

import android.content.Context
import com.looplingua.app.domain.playback.PatternDefinition
import com.looplingua.app.domain.playback.PatternStep
import com.looplingua.app.domain.playback.StepType
import org.json.JSONObject

object PatternLoader {

    fun load(
        context: Context,
        fileName: String
    ): PatternDefinition {

        val jsonString = context.assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }

        val json = JSONObject(jsonString)

        val name = json.getString("name")
        val playbackSpeed =
            json.optDouble("playbackSpeed", 1.0).toFloat()

        val stepsJson = json.getJSONArray("steps")

        val steps = mutableListOf<PatternStep>()

        for (i in 0 until stepsJson.length()) {

            val item = stepsJson.get(i)

            if (item is String) {

                steps.add(
                    PatternStep(
                        type = StepType.valueOf(item)
                    )
                )

            } else {

                val stepJson = stepsJson.getJSONObject(i)

                val type =
                    StepType.valueOf(
                        stepJson.getString("type")
                    )

                val multiplier =
                    stepJson.optDouble(
                        "multiplier",
                        1.0
                    ).toFloat()

                steps.add(
                    PatternStep(
                        type = type,
                        multiplier = multiplier
                    )
                )
            }
        }

        return PatternDefinition(
            name = name,
            steps = steps,
            playbackSpeed = playbackSpeed
        )
    }
}