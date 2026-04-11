/*
 *
 *  *  Copyright (c) 2026 Dhanush Sugganahalli <dhanush41230@gmail.com>
 *  *
 *  *  This program is free software; you can redistribute it and/or modify it under
 *  *  the terms of the GNU General Public License as published by the Free Software
 *  *  Foundation; either version 3 of the License, or (at your option) any later
 *  *  version.
 *  *
 *  *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  *  PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License along with
 *  *  this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.opennotes.feature_node.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.opennotes.feature_node.presentation.settings.Settings
import com.opennotes.feature_node.presentation.settings.ThemeMode
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
        // New theme settings
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val BLACK_THEME = booleanPreferencesKey("black_theme")
        private val BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")

        // Legacy settings for backward compatibility
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
        private val AUTOMATIC_THEME = booleanPreferencesKey("automatic_theme")
        private val LIGHT_THEME = booleanPreferencesKey("LIGHT_THEME")
    }

    /**
     * Save the whole Settings object
     */
    suspend fun saveSettings(settings: Settings) {
        dataStore.edit { prefs ->
            prefs[THEME_MODE] = settings.themeMode.name
            prefs[BLACK_THEME] = settings.blackTheme
            prefs[BIOMETRIC_LOCK] = settings.biometricLock

            // Also update legacy fields for compatibility
            when (settings.themeMode) {
                ThemeMode.SYSTEM -> {
                    prefs[AUTOMATIC_THEME] = true
                    prefs[DARK_THEME] = false
                    prefs[LIGHT_THEME] = false
                }

                ThemeMode.LIGHT -> {
                    prefs[AUTOMATIC_THEME] = false
                    prefs[DARK_THEME] = false
                    prefs[LIGHT_THEME] = true
                }

                ThemeMode.DARK -> {
                    prefs[AUTOMATIC_THEME] = false
                    prefs[DARK_THEME] = true
                    prefs[LIGHT_THEME] = false
                }
            }
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
                val defaultSettings = Settings()

                // Try to read new theme mode first
                val themeModeString = prefs[THEME_MODE]
                val themeMode = if (themeModeString != null) {
                    try {
                        ThemeMode.valueOf(themeModeString)
                    } catch (e: IllegalArgumentException) {
                        // If migration needed from legacy settings
                        migrateLegacyThemeMode(prefs, defaultSettings)
                    }
                } else {
                    // Migration from legacy settings
                    migrateLegacyThemeMode(prefs, defaultSettings)
                }

                Settings(
                    themeMode = themeMode,
                    blackTheme = prefs[BLACK_THEME] ?: defaultSettings.blackTheme,
                    biometricLock = prefs[BIOMETRIC_LOCK] ?: defaultSettings.biometricLock,
                    // Legacy fields for compatibility
                    darkTheme = prefs[DARK_THEME] ?: defaultSettings.darkTheme,
                    systemTheme = prefs[AUTOMATIC_THEME] ?: defaultSettings.systemTheme,
                    lightTheme = prefs[LIGHT_THEME] ?: defaultSettings.lightTheme
                )
            }
    }

    private fun migrateLegacyThemeMode(prefs: Preferences, defaultSettings: Settings): ThemeMode {
        val systemTheme = prefs[AUTOMATIC_THEME] ?: defaultSettings.systemTheme
        val darkTheme = prefs[DARK_THEME] ?: defaultSettings.darkTheme
        val lightTheme = prefs[LIGHT_THEME] ?: defaultSettings.lightTheme

        return when {
            systemTheme -> ThemeMode.SYSTEM
            lightTheme -> ThemeMode.LIGHT
            darkTheme -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    /**
     * Read Settings once (non-Flow) - Alternative approach
     */
    suspend fun getSettings(): Settings {
        val prefs = dataStore.data.first()
        val defaultSettings = Settings()

        val themeModeString = prefs[THEME_MODE]
        val themeMode = if (themeModeString != null) {
            try {
                ThemeMode.valueOf(themeModeString)
            } catch (e: IllegalArgumentException) {
                migrateLegacyThemeMode(prefs, defaultSettings)
            }
        } else {
            migrateLegacyThemeMode(prefs, defaultSettings)
        }

        return Settings(
            themeMode = themeMode,
            blackTheme = prefs[BLACK_THEME] ?: defaultSettings.blackTheme,
            biometricLock = prefs[BIOMETRIC_LOCK] ?: defaultSettings.biometricLock,
            // Legacy fields for compatibility
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
