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

package com.opennotes.feature_node.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opennotes.feature_node.data.repository.DataStoreRepository
import com.opennotes.feature_node.domain.use_case.NoteUseCases
import com.opennotes.feature_node.domain.util.ExportResult
import com.opennotes.feature_node.domain.util.ImportResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val noteUseCases: NoteUseCases,
        private val dataStoreRepository: DataStoreRepository,
    ) : ViewModel() {
        val settings: StateFlow<Settings> =
            dataStoreRepository
                .getSettingsFlow()
                .onEach {
                    if (!_isLoaded.value) _isLoaded.value = true
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Eagerly, // starts collecting immediately, not waiting for subscribers
                    initialValue = Settings(),
                )

        private val _isAppUnlocked = MutableStateFlow(false)
        val isAppUnlocked: StateFlow<Boolean> = _isAppUnlocked.asStateFlow()

        fun setAppUnlocked(unlocked: Boolean) {
            _isAppUnlocked.value = unlocked
        }

        private val _isLoaded = MutableStateFlow(false)
        val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

        private val _uiEvent = Channel<UiEvent>()
        val uiEvent = _uiEvent.receiveAsFlow()

        sealed class UiEvent {
            data class ShowSnackbar(
                val message: String,
            ) : UiEvent()

            data class OpenExportPicker(
                val suggestedFileName: String,
            ) : UiEvent()

            object RequestBiometricAuthForEnable : UiEvent()
        }

        fun updateThemeMode(themeMode: ThemeMode) {
            val newSettings = settings.value.copy(themeMode = themeMode)
            viewModelScope.launch {
                dataStoreRepository.saveSettings(newSettings)
            }
        }

        fun updateBlackTheme(blackTheme: Boolean) {
            val newSettings = settings.value.copy(blackTheme = blackTheme)
            viewModelScope.launch {
                dataStoreRepository.saveSettings(newSettings)
            }
        }

        fun updateColorScheme(colorLong: Long) {
            val newSettings = settings.value.copy(colorScheme = colorLong)
            viewModelScope.launch {
                dataStoreRepository.saveSettings(newSettings)
            }
        }

        fun onBiometricLockToggleRequest(enable: Boolean) {
            if (!enable) {
                viewModelScope.launch {
                    dataStoreRepository.saveSettings(settings.value.copy(biometricLock = false))
                }
                return
            }

            viewModelScope.launch {
                _uiEvent.send(UiEvent.RequestBiometricAuthForEnable)
            }
        }

        fun onBiometricAuthSuccess() {
            viewModelScope.launch {
                dataStoreRepository.saveSettings(settings.value.copy(biometricLock = true))
                _uiEvent.send(UiEvent.ShowSnackbar("Biometric lock enabled"))
            }
        }

        fun onBiometricAuthFailed() {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar("Biometric authentication cancelled or failed"))
            }
        }

        fun onExportClick() {
            viewModelScope.launch {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val backupFileName = "notes_backup_$timestamp.json"
                _uiEvent.send(UiEvent.OpenExportPicker(backupFileName))
            }
        }

        fun onExportUriSelected(fileUri: Uri) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = noteUseCases.exportNotes(fileUri)) {
                    is ExportResult.Success ->
                        _uiEvent.send(
                            UiEvent.ShowSnackbar("Notes exported successfully"),
                        )
                    is ExportResult.Error -> _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                }
            }
        }

        fun onImportClick(fileUri: Uri) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = noteUseCases.importNotes(fileUri)) {
                    is ImportResult.Success ->
                        _uiEvent.send(UiEvent.ShowSnackbar("Notes imported"))
                    is ImportResult.Error ->
                        _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                }
            }
        }
    }
