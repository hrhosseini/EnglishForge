package com.example.englishvocabulary.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.englishvocabulary.core.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

class SettingsDataStore(private val context: Context) {
    companion object {
        private val KEY_BASE_URL = stringPreferencesKey(Constants.PREF_KEY_BASE_URL)
    }

    val baseUrl: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_BASE_URL] ?: Constants.DEFAULT_EMULATOR_URL
    }

    suspend fun saveBaseUrl(url: String) {
        val sanitized = if (url.endsWith("/")) url else "$url/"
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_BASE_URL] = sanitized
        }
    }
}
