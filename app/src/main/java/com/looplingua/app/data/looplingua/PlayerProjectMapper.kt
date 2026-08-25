package com.looplingua.app.data.looplingua

import com.looplingua.app.domain.model.Segment
import com.looplingua.app.domain.model.Track
import com.looplingua.app.domain.model.TrackWithSegments
import java.io.File

class PlayerProjectMapper {

    fun map(
        project: LoopLinguaProject,
        projectDirectory: File
    ): List<TrackWithSegments> {

        return project.tracks.map { loopLinguaTrack ->

            val audioFile = File(
                projectDirectory,
                "Audio/${loopLinguaTrack.fileName}"
            )

            val track = Track(
                id = loopLinguaTrack.trackId,
                title = loopLinguaTrack.fileName,
                originalAudioPath = audioFile.absolutePath,
                translationAudioPath = null,
                memoAudioPath = null
            )

            val segments =
                loopLinguaTrack.segments.map { loopLinguaSegment ->

                    Segment(
                        id = loopLinguaSegment.segmentId,

                        originalStartMs =
                            loopLinguaSegment.originalStartMs,

                        originalEndMs =
                            loopLinguaSegment.originalEndMs,

                        // TTSはまだ存在しない
                        translationStartMs = 0,
                        translationEndMs = 0,

                        memoStartMs = 0,
                        memoEndMs = 0,

                        originalText =
                            loopLinguaSegment.originalUser
                                .ifEmpty {
                                    loopLinguaSegment.originalAuto
                                },

                        translationText =
                            loopLinguaSegment.translationUser
                                .ifEmpty {
                                    loopLinguaSegment.translationAuto
                                },

                        memoAuto = loopLinguaSegment.memoAuto,
                        memoUser = loopLinguaSegment.memoUser,

                        flagged = loopLinguaSegment.flagged,
                        skipped = loopLinguaSegment.skipped
                    )
                }

            TrackWithSegments(
                track = track,
                segments = segments
            )
        }
    }
}