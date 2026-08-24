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

        val shortPauseMultiplier: Preferences.Key<Float> =
            floatPreferencesKey("short_pause_multiplier")

        val longPauseMultiplier: Preferences.Key<Float> =
            floatPreferencesKey("long_pause_multiplier")
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

    val playbackSpeed: Flow<Float?> =
        context.playerPreferencesDataStore.data.map { preferences ->
            preferences[Keys.playbackSpeed]
        }

    suspend fun savePlaybackSpeed(
        speed: Float
    ) {
        context.playerPreferencesDataStore.edit { preferences ->
            preferences[Keys.playbackSpeed] = speed
        }
    }

    // ============================================================
    // Pause Multiplier
    // ============================================================

    val shortPauseMultiplier: Flow<Float?> =
        context.playerPreferencesDataStore.data.map { preferences ->
            preferences[Keys.shortPauseMultiplier]
        }

    val longPauseMultiplier: Flow<Float?> =
        context.playerPreferencesDataStore.data.map { preferences ->
            preferences[Keys.longPauseMultiplier]
        }

    suspend fun saveShortPauseMultiplier(
        multiplier: Float
    ) {
        context.playerPreferencesDataStore.edit { preferences ->
            preferences[Keys.shortPauseMultiplier] = multiplier
        }
    }

    suspend fun saveLongPauseMultiplier(
        multiplier: Float
    ) {
        context.playerPreferencesDataStore.edit { preferences ->
            preferences[Keys.longPauseMultiplier] = multiplier
        }
    }

    suspend fun clearPlaybackSpeed() {
        context.playerPreferencesDataStore.edit { preferences ->
            preferences.remove(Keys.playbackSpeed)
        }
    }

    suspend fun clearShortPauseMultiplier() {
        context.playerPreferencesDataStore.edit { preferences ->
            preferences.remove(Keys.shortPauseMultiplier)
        }
    }

    suspend fun clearLongPauseMultiplier() {
        context.playerPreferencesDataStore.edit { preferences ->
            preferences.remove(Keys.longPauseMultiplier)
        }
    }
}