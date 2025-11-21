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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    // Directly expose the DataStore Flow - no need for MutableStateFlow
    val settings: StateFlow<Settings> = dataStoreRepository.getSettingsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings() // Default settings while loading
        )

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

            settings.first()
            _isLoaded.value = true
        }
    }

    fun updateSettings(update: (Settings) -> Settings) {
        val newSettings = update(settings.value)
        viewModelScope.launch {
            dataStoreRepository.saveSettings(newSettings)
        }
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
