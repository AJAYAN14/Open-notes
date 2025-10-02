package com.opennotes.feature_node.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.opennotes.feature_node.presentation.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
        private val AUTOMATIC_THEME = booleanPreferencesKey("automatic_theme")
        private val  LIGHT_THEME = booleanPreferencesKey("LIGHT_THEME")
    }

    /**
     * Save the whole Settings object
     */
    suspend fun saveSettings(settings: Settings) {
        dataStore.edit { prefs ->
            prefs[DARK_THEME] = settings.darkTheme
            prefs[AUTOMATIC_THEME] = settings.systemTheme
            prefs[LIGHT_THEME] = settings.lightTheme
        }
    }

    /**
     * Read Settings as Flow
     */
    fun getSettingsFlow(): Flow<Settings> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else throw exception
            }
            .map { prefs ->
                val defaultSettings = Settings() // Use Settings class defaults
                Settings(
                    darkTheme = prefs[DARK_THEME] ?: defaultSettings.darkTheme,
                    systemTheme = prefs[AUTOMATIC_THEME] ?: defaultSettings.systemTheme,
                    lightTheme = prefs[LIGHT_THEME] ?: defaultSettings.lightTheme
                )
            }
    }

    /**
     * Read Settings once (non-Flow) - Alternative approach
     */
    suspend fun getSettings(): Settings {
        val prefs = dataStore.data.first()
        val defaultSettings = Settings() // Use Settings class defaults
        return Settings(
            darkTheme = prefs[DARK_THEME] ?: defaultSettings.darkTheme,
            systemTheme = prefs[AUTOMATIC_THEME] ?: defaultSettings.systemTheme,
            lightTheme = prefs[LIGHT_THEME] ?: defaultSettings.lightTheme
        )
    }

    /**
     * Clear all preferences
     */
    suspend fun clearAll() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
