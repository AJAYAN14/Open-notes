package com.opennotes.feature_node.presentation.settings

import android.net.Uri
// androidx.compose.runtime.State // Not needed if exposing StateFlow directly
// import androidx.compose.runtime.mutableStateOf // Not needed if exposing StateFlow directly
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val dataStoreRepository: DataStoreRepository // Assuming this repository has getSettings() and saveSettings(Settings)
) : ViewModel() {

    private val _settings = MutableStateFlow(Settings()) // Initialize with default Settings
    val settings: StateFlow<Settings> = _settings.asStateFlow()


    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class ShowShareDialog(val uri: Uri) : UiEvent()
    }

    init {
        viewModelScope.launch {
            // Load initial settings from DataStore
            // Assuming dataStoreRepository.getSettings() is a suspend function that returns Settings
            // Or use dataStoreRepository.getSettingsFlow().first() if it returns a Flow
            _settings.value = dataStoreRepository.getSettings()
            _isLoaded.value = true
        }
    }

    /**
     * Toggles the theme setting through System -> Light -> Dark -> System.
     * This manipulates the `automaticTheme` and `darkTheme` booleans in the Settings object.
     */
    fun onThemeToggle() {
        val currentSettings = _settings.value
        val newSettings = when {
            // Current is System (automatic is true) -> Next is Light
            currentSettings.automaticTheme -> {
                currentSettings.copy(automaticTheme = false, darkTheme = false)
            }
            // Current is Light (automatic is false, darkTheme is false) -> Next is Dark
            !currentSettings.automaticTheme && !currentSettings.darkTheme -> {
                currentSettings.copy(automaticTheme = false, darkTheme = true)
            }
            // Current is Dark (automatic is false, darkTheme is true) -> Next is System
            !currentSettings.automaticTheme && currentSettings.darkTheme -> {
                currentSettings.copy(automaticTheme = true, darkTheme = false) // When going to system, darkTheme can be false
            }
            // Fallback to a default state (e.g., System) if current state is unexpected
            else -> {
                currentSettings.copy(automaticTheme = true, darkTheme = false)
            }
        }
        _settings.value = newSettings
        viewModelScope.launch {
            dataStoreRepository.saveSettings(newSettings)
        }
    }

    /** Generic update function for any setting - can be used by other settings if needed */
    fun updateSettings(update: (Settings) -> Settings) {
        val newSettings = update(_settings.value)
        _settings.value = newSettings
        viewModelScope.launch {
            dataStoreRepository.saveSettings(newSettings)
        }
    }


    fun onExportClick() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = noteUseCases.exportNotes()) {
                is ExportResult.Success -> _uiEvent.send(UiEvent.ShowShareDialog(result.uri))
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
