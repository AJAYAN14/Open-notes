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
        private val DYNAMIC_THEME = booleanPreferencesKey("dynamic_theme")
    }

    /**
     * Save the whole Settings object
     */
    suspend fun saveSettings(settings: Settings) {
        dataStore.edit { prefs ->
            prefs[DARK_THEME] = settings.darkTheme
            prefs[AUTOMATIC_THEME] = settings.automaticTheme
            prefs[DYNAMIC_THEME] = settings.dynamicTheme
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
                Settings(
                    darkTheme = prefs[DARK_THEME] ?: false,
                    automaticTheme = prefs[AUTOMATIC_THEME] ?: false,
                    dynamicTheme = prefs[DYNAMIC_THEME] ?: false
                )
            }
    }

    /**
     * Read Settings once (non-Flow)
     */
    suspend fun getSettings(): Settings {
        val prefs = dataStore.data.first()
        return Settings(
            darkTheme = prefs[DARK_THEME] ?: false,
            automaticTheme = prefs[AUTOMATIC_THEME] ?: false,
            dynamicTheme = prefs[DYNAMIC_THEME] ?: false
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
