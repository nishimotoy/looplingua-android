package com.looplingua.tool

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class LoopLinguaProject(
    val formatVersion: String = "1.0",
    val projectId: String,
    val projectName: String,
    val tracks: List<LoopLinguaTrack>
)

@Serializable
data class LoopLinguaTrack(
    val trackId: Int,
    val fileName: String,
    val originalLang: String,
    val translationLang: String,
    val segments: List<LoopLinguaSegment>
)

@Serializable
data class LoopLinguaSegment(
    val segmentId: Int,
    val originalStartMs: Long,
    val originalEndMs: Long,
    val originalAuto: String,
    val originalUser: String = "",
    val translationAuto: String = "",
    val translationUser: String = "",
    val memoUser: String = ""
)

fun main() {

    val projectFile = File(
        "testdata/project/20260812010803-青本ウクライナ語/青本ウクライナ語.looplingua"
    )

    require(projectFile.isFile) {
        "Project file not found: ${projectFile.path}"
    }

    val json = Json {
        ignoreUnknownKeys = true
    }

    val project =
        json.decodeFromString<LoopLinguaProject>(
            projectFile.readText()
        )

    println("==================================================")
    println("Timestamp Analyzer")
    println("Project : ${project.projectName}")
    println("Tracks  : ${project.tracks.size}")
    println("==================================================")
    println()

    var tracksWithIssues = 0
    var totalIssues = 0

    for (track in project.tracks) {

        val issues = mutableListOf<String>()

        for (segment in track.segments) {

            val duration =
                segment.originalEndMs - segment.originalStartMs

            // 1. Empty original text
            if (segment.originalAuto.isBlank()) {
                issues +=
                    "SEGMENT ${segment.segmentId}: EMPTY_TEXT"
            }

            // 2. Zero or negative duration
            if (duration <= 0) {
                issues +=
                    "SEGMENT ${segment.segmentId}: " +
                            "ZERO_OR_NEGATIVE_DURATION " +
                            "(${segment.originalStartMs}-${segment.originalEndMs})"
            }

            // 3. Very short segment
            if (duration > 0 && duration < 500) {
                issues +=
                    "SEGMENT ${segment.segmentId}: " +
                            "SHORT_SEGMENT ${duration}ms"
            }
        }

        // 4. Timestamp overlap with previous segment
        for (index in 1 until track.segments.size) {

            val previous = track.segments[index - 1]
            val current = track.segments[index]

            if (current.originalStartMs < previous.originalEndMs) {

                val overlap =
                    previous.originalEndMs - current.originalStartMs

                issues +=
                    "SEGMENT ${current.segmentId}: " +
                            "OVERLAP ${overlap}ms " +
                            "with SEGMENT ${previous.segmentId}"
            }
        }

        // 5. Timestamp reversal
        for (index in 1 until track.segments.size) {

            val previous = track.segments[index - 1]
            val current = track.segments[index]

            if (current.originalStartMs < previous.originalStartMs) {

                issues +=
                    "SEGMENT ${current.segmentId}: " +
                            "TIMESTAMP_REVERSED " +
                            "(${current.originalStartMs} < " +
                            "${previous.originalStartMs})"
            }
        }

        if (issues.isNotEmpty()) {

            tracksWithIssues++
            totalIssues += issues.size

            println("--------------------------------------------------")
            println(
                "Track ${track.trackId}: ${track.fileName}"
            )

            for (issue in issues) {
                println("  $issue")
            }
        }
    }

    println()
    println("==================================================")
    println("Summary")
    println("Tracks analyzed   : ${project.tracks.size}")
    println("Tracks with issues: $tracksWithIssues")
    println("Total issues      : $totalIssues")
    println("==================================================")
}