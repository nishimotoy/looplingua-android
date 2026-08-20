package com.looplingua.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.looplingua.app.domain.playback.Pattern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.playerPreferencesDataStore by preferencesDataStore(
    name = "player_preferences"
)

class PlayerPreferences(
    private val context: Context
) {

    private object Keys {

        val lastProjectId: Preferences.Key<String> =
            stringPreferencesKey("last_project_id")

        val lastTrackId: Preferences.Key<Int> =
            intPreferencesKey("last_track_id")

        val lastSegmentId: Preferences.Key<Int> =
            intPreferencesKey("last_segment_id")

        val playbackPattern: Preferences.Key<String> =
            stringPreferencesKey("playback_pattern")

        val playbackSpeed: Preferences.Key<Float> =
            floatPreferencesKey("playback_speed")
    }

    // ============================================================
    // Playback Position
    // ============================================================

    data class PlaybackPosition(
        val projectId: String,
        val trackId: Int,
        val segmentId: Int
    )

    val playbackPosition: Flow<PlaybackPosition?> =
        context.playerPreferencesDataStore.data.map { preferences ->

            val projectId = preferences[Keys.lastProjectId]
            val trackId = preferences[Keys.lastTrackId]
            val segmentId = preferences[Keys.lastSegmentId]

            if (
                projectId != null &&
                trackId != null &&
                segmentId != null
            ) {
                PlaybackPosition(
                    projectId = projectId,
                    trackId = trackId,
                    segmentId = segmentId
                )
            } else {
                null
            }
        }

    suspend fun savePlaybackPosition(
        projectId: String,
        trackId: Int,
        segmentId: Int
    ) {

        context.playerPreferencesDataStore.edit { preferences ->

            preferences[Keys.lastProjectId] = projectId
            preferences[Keys.lastTrackId] = trackId
            preferences[Keys.lastSegmentId] = segmentId
        }
    }

    // ============================================================
    // Playback Pattern
    // ============================================================

    val playbackPattern: Flow<Pattern> =
        context.playerPreferencesDataStore.data.map { preferences ->

            val name = preferences[Keys.playbackPattern]

            name?.let {
                runCatching {
                    Pattern.valueOf(it)
                }.getOrNull()
            } ?: Pattern.BASIC
        }

    suspend fun savePlaybackPattern(
        pattern: Pattern
    ) {
        context.playerPreferencesDataStore.edit { preferences ->
            preferences[Keys.playbackPattern] = pattern.name
        }
    }

    // ============================================================
    // Playback Speed
    // ============================================================

    val playbackSpeed: Flow<Float> =
        context.playerPreferencesDataStore.data.map { preferences ->
            preferences[Keys.playbackSpeed] ?: 1.0f
        }

    suspend fun savePlaybackSpeed(
        speed: Float
    ) {
        context.playerPreferencesDataStore.edit { preferences ->
            preferences[Keys.playbackSpeed] = speed
        }
    }
}