package com.looplingua.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
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

        val lastTrackId: Preferences.Key<Long> =
            longPreferencesKey("last_track_id")

        val lastSegmentId: Preferences.Key<Long> =
            longPreferencesKey("last_segment_id")

        val playbackPattern: Preferences.Key<String> =
            stringPreferencesKey("playback_pattern")
    }

    data class PlaybackPosition(
        val trackId: Long,
        val segmentId: Long
    )

    val playbackPosition: Flow<PlaybackPosition?> =
        context.playerPreferencesDataStore.data.map { preferences ->

            val trackId =
                preferences[Keys.lastTrackId]

            val segmentId =
                preferences[Keys.lastSegmentId]

            if (trackId != null && segmentId != null) {
                PlaybackPosition(
                    trackId = trackId,
                    segmentId = segmentId
                )
            } else {
                null
            }
        }

    val playbackPattern: Flow<Pattern> =
        context.playerPreferencesDataStore.data.map { preferences ->

            val name =
                preferences[Keys.playbackPattern]

            name?.let {
                runCatching {
                    Pattern.valueOf(it)
                }.getOrNull()
            } ?: Pattern.BASIC
        }

    suspend fun savePlaybackPosition(
        trackId: Long,
        segmentId: Long
    ) {
        context.playerPreferencesDataStore.edit { preferences ->

            preferences[Keys.lastTrackId] = trackId
            preferences[Keys.lastSegmentId] = segmentId
        }
    }

    suspend fun savePlaybackPattern(
        pattern: Pattern
    ) {
        context.playerPreferencesDataStore.edit { preferences ->

            preferences[Keys.playbackPattern] =
                pattern.name
        }
    }
}