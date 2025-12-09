package com.breathing.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.breathing.app.models.Preset
import com.breathing.app.models.SessionRecord
import com.breathing.app.models.AudioSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "breathing_app")

class DataStore(context: Context) {
    private val dataStore = context.dataStore
    private val gson = Gson()

    companion object {
        private val USER_PRESETS_KEY = stringPreferencesKey("user_presets")
        private val BREATHING_HISTORY_KEY = stringPreferencesKey("breathing_history")
        private val AUDIO_SETTINGS_KEY = stringPreferencesKey("audio_settings")
    }

    // Presets
    suspend fun savePresets(presets: List<Preset>) {
        val userPresets = presets.filter { !it.isDefault }
        dataStore.edit { preferences ->
            preferences[USER_PRESETS_KEY] = gson.toJson(userPresets)
        }
    }

    suspend fun getPresets(defaultPresets: List<Preset>): List<Preset> {
        val preferences = dataStore.data.first()
        val userPresetsJson = preferences[USER_PRESETS_KEY] ?: return defaultPresets
        val type = object : TypeToken<List<Preset>>() {}.type
        val userPresets: List<Preset> = gson.fromJson(userPresetsJson, type)
        return defaultPresets + userPresets
    }

    // History
    suspend fun saveSessionRecord(record: SessionRecord) {
        val history = getHistory()
        val updated = (listOf(record) + history).take(100) // Keep only last 100
        dataStore.edit { preferences ->
            preferences[BREATHING_HISTORY_KEY] = gson.toJson(updated)
        }
    }

    suspend fun getHistory(): List<SessionRecord> {
        val preferences = dataStore.data.first()
        val historyJson = preferences[BREATHING_HISTORY_KEY] ?: return emptyList()
        val type = object : TypeToken<List<SessionRecord>>() {}.type
        return gson.fromJson(historyJson, type)
    }

    fun getHistoryFlow(): Flow<List<SessionRecord>> {
        return dataStore.data.map { preferences ->
            val historyJson = preferences[BREATHING_HISTORY_KEY] ?: return@map emptyList()
            val type = object : TypeToken<List<SessionRecord>>() {}.type
            gson.fromJson(historyJson, type)
        }
    }

    // Audio Settings
    suspend fun saveAudioSettings(settings: AudioSettings) {
        dataStore.edit { preferences ->
            preferences[AUDIO_SETTINGS_KEY] = gson.toJson(settings)
        }
    }

    fun getAudioSettingsFlow(): Flow<AudioSettings> {
        return dataStore.data.map { preferences ->
            val settingsJson = preferences[AUDIO_SETTINGS_KEY]
            if (settingsJson != null) {
                gson.fromJson(settingsJson, AudioSettings::class.java)
            } else {
                AudioSettings()
            }
        }
    }

    suspend fun getAudioSettings(): AudioSettings {
        val preferences = dataStore.data.first()
        val settingsJson = preferences[AUDIO_SETTINGS_KEY]
        return if (settingsJson != null) {
            gson.fromJson(settingsJson, AudioSettings::class.java)
        } else {
            AudioSettings()
        }
    }
}

