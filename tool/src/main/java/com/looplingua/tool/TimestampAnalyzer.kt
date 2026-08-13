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

    //------------------------------------------------------------------
    // Project file
    //------------------------------------------------------------------

    val projectFile = File(
        "testdata/project/20260812010803-青本ウクライナ語/青本ウクライナ語.looplingua"
    )

    require(projectFile.isFile) {
        "Project file not found: ${projectFile.path}"
    }

    //------------------------------------------------------------------
    // JSON
    //------------------------------------------------------------------

    val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    val project =
        json.decodeFromString<LoopLinguaProject>(
            projectFile.readText()
        )

    //------------------------------------------------------------------
    // Analyzer
    //------------------------------------------------------------------

    println("==================================================")
    println("Timestamp Analyzer")
    println("Project : ${project.projectName}")
    println("ProjectID: ${project.projectId}")
    println("Tracks  : ${project.tracks.size}")
    println("==================================================")
    println()

    var tracksWithIssues = 0
    var totalIssues = 0

    val issueCounts =
        linkedMapOf<String, Int>()

    val analyzedTracks =
        mutableListOf<LoopLinguaTrack>()

    //------------------------------------------------------------------
    // Track loop
    //------------------------------------------------------------------

    for (track in project.tracks) {

        var trackIssueCount = 0

        val analyzedSegments =
            track.segments.mapIndexed { index, current ->

                val issues =
                    mutableListOf<String>()

                val duration =
                    current.originalEndMs -
                            current.originalStartMs

                //------------------------------------------------------
                // 1. Empty original text
                //------------------------------------------------------

                if (current.originalAuto.isBlank()) {

                    issues += "EMPTY_TEXT"
                }

                //------------------------------------------------------
                // 2. Zero or negative duration
                //------------------------------------------------------

                if (duration <= 0) {

                    issues +=
                        "ZERO_OR_NEGATIVE_DURATION " +
                                "(${current.originalStartMs}-${current.originalEndMs})"
                }

                //------------------------------------------------------
                // 3. Very short segment
                //------------------------------------------------------

                if (duration in 1..499) {

                    issues +=
                        "SHORT_SEGMENT ${duration}ms"
                }

                //------------------------------------------------------
                // 4 + 5. Compare with previous segment
                //
                // TIMESTAMP_REVERSED takes precedence over OVERLAP.
                //------------------------------------------------------

                if (index > 0) {

                    val previous =
                        track.segments[index - 1]

                    if (
                        current.originalStartMs <
                        previous.originalStartMs
                    ) {

                        issues +=
                            "TIMESTAMP_REVERSED " +
                                    "(${current.originalStartMs} < " +
                                    "${previous.originalStartMs})"

                    } else if (
                        current.originalStartMs <
                        previous.originalEndMs
                    ) {

                        val overlap =
                            previous.originalEndMs -
                                    current.originalStartMs

                        issues +=
                            "OVERLAP ${overlap}ms " +
                                    "with SEGMENT ${previous.segmentId}"
                    }
                }

                //------------------------------------------------------
                // Update statistics
                //------------------------------------------------------

                if (issues.isNotEmpty()) {

                    trackIssueCount += issues.size
                    totalIssues += issues.size

                    for (issue in issues) {

                        val issueType =
                            issue.substringBefore(" ")

                        issueCounts[issueType] =
                            (issueCounts[issueType] ?: 0) + 1
                    }
                }

                //------------------------------------------------------
                // Add issues to memoUser
                //
                // Existing user memo is preserved.
                // Existing identical analyzer messages are not added
                // again, making the analyzer effectively idempotent.
                //------------------------------------------------------

                var memo =
                    current.memoUser

                for (issue in issues) {

                    val memoLine =
                        "[ANALYZER] $issue"

                    val alreadyExists =
                        memo.lines().any {
                            it == memoLine
                        }

                    if (!alreadyExists) {

                        memo =
                            if (memo.isBlank()) {
                                memoLine
                            } else {
                                "$memo\n$memoLine"
                            }
                    }
                }

                current.copy(
                    memoUser = memo
                )
            }

        //--------------------------------------------------------------
        // Track statistics
        //--------------------------------------------------------------

        if (trackIssueCount > 0) {

            tracksWithIssues++

            println("--------------------------------------------------")
            println(
                "Track ${track.trackId}: ${track.fileName}"
            )

            println(
                "Segments: ${track.segments.size}"
            )

            println(
                "Issues  : $trackIssueCount"
            )

            for (segment in analyzedSegments) {

                val analyzerMemos =
                    segment.memoUser
                        .lines()
                        .filter {
                            it.startsWith("[ANALYZER]")
                        }

                if (analyzerMemos.isNotEmpty()) {

                    println(
                        "  Segment ${segment.segmentId}:"
                    )

                    for (memo in analyzerMemos) {

                        println(
                            "    ${memo.removePrefix("[ANALYZER] ")}"
                        )
                    }
                }
            }
        }

        analyzedTracks +=
            track.copy(
                segments = analyzedSegments
            )
    }

    //------------------------------------------------------------------
    // Create analyzed project
    //------------------------------------------------------------------

    val analyzedProject =
        project.copy(
            tracks = analyzedTracks
        )

    //------------------------------------------------------------------
    // Write modified .looplingua
    //------------------------------------------------------------------

    projectFile.writeText(
        json.encodeToString(analyzedProject)
    )

    //------------------------------------------------------------------
    // Console summary
    //------------------------------------------------------------------

    println()
    println("==================================================")
    println("Summary")
    println("Tracks analyzed   : ${project.tracks.size}")
    println("Tracks with issues: $tracksWithIssues")
    println("Total issues      : $totalIssues")
    println()

    if (issueCounts.isNotEmpty()) {

        println("Issues by type:")

        for ((type, count) in issueCounts) {

            println(
                "  %-28s : %d".format(
                    type,
                    count
                )
            )
        }
    }

    println("==================================================")
    println()
    println(
        "Updated project:"
    )
    println(
        "  ${projectFile.path}"
    )

    //------------------------------------------------------------------
    // Write report
    //------------------------------------------------------------------

    val outputDirectory =
        File("testdata/output")

    outputDirectory.mkdirs()

    val reportFile =
        File(
            outputDirectory,
            "analyzer_${project.projectId}_${project.projectName}.log"
        )

    val report =
        buildString {

            appendLine("==================================================")
            appendLine("Timestamp Analyzer")
            appendLine("==================================================")
            appendLine()
            appendLine(
                "ProjectID         : ${project.projectId}"
            )
            appendLine(
                "ProjectName       : ${project.projectName}"
            )
            appendLine(
                "Project file      : ${projectFile.path}"
            )
            appendLine()
            appendLine(
                "Tracks analyzed   : ${project.tracks.size}"
            )
            appendLine(
                "Tracks with issues: $tracksWithIssues"
            )
            appendLine(
                "Total issues      : $totalIssues"
            )
            appendLine()

            appendLine("Issues by type:")
            appendLine()

            if (issueCounts.isEmpty()) {

                appendLine(
                    "  No issues found."
                )

            } else {

                for ((type, count) in issueCounts) {

                    appendLine(
                        "  %-28s : %d".format(
                            type,
                            count
                        )
                    )
                }
            }

            appendLine()
            appendLine("==================================================")
            appendLine("Track Details")
            appendLine("==================================================")
            appendLine()

            for (track in analyzedProject.tracks) {

                val issueSegments =
                    track.segments.filter { segment ->

                        segment.memoUser
                            .lines()
                            .any {
                                it.startsWith("[ANALYZER]")
                            }
                    }

                if (issueSegments.isEmpty()) {
                    continue
                }

                appendLine(
                    "Track ${track.trackId}: ${track.fileName}"
                )

                appendLine(
                    "Segments: ${track.segments.size}"
                )

                appendLine(
                    "Issue segments: ${issueSegments.size}"
                )

                appendLine()

                for (segment in issueSegments) {

                    appendLine(
                        "  SEGMENT ${segment.segmentId}"
                    )

                    appendLine(
                        "    Start-End : " +
                                "${segment.originalStartMs}-" +
                                "${segment.originalEndMs}"
                    )

                    appendLine(
                        "    Text      : " +
                                segment.originalAuto
                    )

                    val analyzerMemos =
                        segment.memoUser
                            .lines()
                            .filter {
                                it.startsWith("[ANALYZER]")
                            }

                    for (memo in analyzerMemos) {

                        appendLine(
                            "    ${memo.removePrefix("[ANALYZER] ")}"
                        )
                    }

                    appendLine()
                }

                appendLine(
                    "--------------------------------------------------"
                )
                appendLine()
            }
        }

    reportFile.writeText(report)

    println(
        "Report:"
    )
    println(
        "  ${reportFile.path}"
    )
}