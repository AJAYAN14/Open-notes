package com.opennotes.feature_node.presentation.settings

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opennotes.feature_node.domain.use_case.NoteUseCases
import com.opennotes.feature_node.domain.util.ExportResult
import com.opennotes.feature_node.domain.util.ImportResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _settingsState = mutableStateOf(Settings())
    val settingsState: State<Settings> = _settingsState

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class ShowShareDialog(val uri: Uri) : UiEvent()
    }

    // FIX: Corrected syntax for toggling the boolean
    fun onThemeToggle() {
        _settingsState.value = _settingsState.value.copy(
            darkTheme = !_settingsState.value.darkTheme
        )
    }

    fun onExportClick() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = noteUseCases.exportNotes()) {
                is ExportResult.Success -> {
                    _uiEvent.send(UiEvent.ShowShareDialog(uri = result.uri))
                }
                is ExportResult.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(message = result.message))
                }
            }
        }
    }

    fun onImportClick(fileUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = noteUseCases.importNotes(fileUri)) {
                is ImportResult.Success -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(message = "notes imported"))
                }
                is ImportResult.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(message = result.message))
                }
            }
        }
    }
}