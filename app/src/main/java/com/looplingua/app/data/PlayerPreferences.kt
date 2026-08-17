package com.looplingua.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.looplingua.app.domain.playback.Pattern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.playerDataStore by preferencesDataStore(
    name = "player_preferences"
)

class PlayerPreferences(
    private val context: Context
) {

    // ============================================================
    // Playback Position
    // ============================================================

    data class PlaybackPosition(
        val trackId: Int,
        val segmentId: Int
    )

    private val lastTrackIdKey =
        intPreferencesKey("last_track_id")

    private val lastSegmentIdKey =
        intPreferencesKey("last_segment_id")

    val playbackPosition: Flow<PlaybackPosition?> =
        context.playerDataStore.data.map { preferences ->

            val trackId =
                preferences[lastTrackIdKey]

            val segmentId =
                preferences[lastSegmentIdKey]

            if (
                trackId != null &&
                segmentId != null
            ) {
                PlaybackPosition(
                    trackId = trackId,
                    segmentId = segmentId
                )
            } else {
                null
            }
        }

    suspend fun savePlaybackPosition(
        trackId: Int,
        segmentId: Int
    ) {
        context.playerDataStore.edit { preferences ->

            preferences[lastTrackIdKey] =
                trackId

            preferences[lastSegmentIdKey] =
                segmentId
        }
    }

    // ============================================================
    // Playback Pattern
    // ============================================================

    private val playbackPatternKey =
        stringPreferencesKey("playback_pattern")

    val playbackPattern: Flow<Pattern> =
        context.playerDataStore.data.map { preferences ->

            preferences[playbackPatternKey]
                ?.let { value ->
                    runCatching {
                        Pattern.valueOf(value)
                    }.getOrNull()
                }
                ?: Pattern.BASIC
        }

    suspend fun savePlaybackPattern(
        pattern: Pattern
    ) {
        context.playerDataStore.edit { preferences ->

            preferences[playbackPatternKey] =
                pattern.name
        }
    }
}