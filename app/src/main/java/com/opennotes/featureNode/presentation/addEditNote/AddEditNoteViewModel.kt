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

package com.opennotes.featureNode.presentation.addEditNote

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opennotes.featureNode.domain.model.InvalidNoteException
import com.opennotes.featureNode.domain.model.Note
import com.opennotes.featureNode.domain.usecase.NoteUseCases
import com.opennotes.ui.theme.NoteColorPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel
    @Inject
    constructor(
        private val noteUseCases: NoteUseCases,
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _noteTitle =
            mutableStateOf(
                NoteTextFieldState(
                    text = savedStateHandle.get<String>("title") ?: "",
                    hint = "Enter title ....",
                ),
            )
        val noteTitle: State<NoteTextFieldState> = _noteTitle

        private val _noteContent =
            mutableStateOf(
                NoteTextFieldState(
                    text = savedStateHandle.get<String>("content") ?: "",
                    hint = "Enter content....",
                ),
            )
        val noteContent: State<NoteTextFieldState> = _noteContent

        private val _noteColor =
            mutableIntStateOf(
                savedStateHandle.get<Int>("color") ?: NoteColorPalette.Light.first().toArgb(),
            )
        val noteColor: State<Int> = _noteColor

        private val _eventFlow = MutableSharedFlow<UiEvent>()
        val eventFlow = _eventFlow.asSharedFlow()

        private var currentNoteId: Int? = null

        init {
            savedStateHandle.get<Int>("noteId")?.let { noteId ->
                if (noteId != -1) {
                    viewModelScope.launch {
                        noteUseCases.getNote(noteId)?.also { note ->
                            currentNoteId = note.id
                            if (savedStateHandle.get<String>("title") == null) {
                                _noteTitle.value =
                                    noteTitle.value.copy(
                                        text = note.title,
                                        isHintVisible = false,
                                    )
                                _noteContent.value =
                                    _noteContent.value.copy(
                                        text = note.content,
                                        isHintVisible = false,
                                    )

                                if (savedStateHandle.get<Int>("color") == null) {
                                    _noteColor.intValue = note.color
                                }
                            }
                        }
                    }
                }
            }
        }

        fun applyDefaultColor(isDarkTheme: Boolean) {
            if (_noteColor.intValue == NoteColorPalette.Light.first().toArgb()) {
                _noteColor.intValue =
                    if (isDarkTheme) {
                        NoteColorPalette.Dark.first().toArgb()
                    } else {
                        NoteColorPalette.Light.first().toArgb()
                    }
            }
        }

        fun onEvent(event: AddEditNoteEvent) {
            when (event) {
                is AddEditNoteEvent.EnteredTitle -> {
                    _noteTitle.value = noteTitle.value.copy(text = event.value)
                    savedStateHandle["title"] = event.value // persist to SavedStateHandle
                }
                is AddEditNoteEvent.changeTitleFocus -> {
                    _noteTitle.value =
                        noteTitle.value.copy(
                            isHintVisible = !event.focusState.isFocused && _noteTitle.value.text.isBlank(),
                        )
                }
                is AddEditNoteEvent.EnteredContent -> {
                    _noteContent.value = _noteContent.value.copy(text = event.value)
                    savedStateHandle["content"] = event.value // persist to SavedStateHandle
                }
                is AddEditNoteEvent.changeColor -> {
                    _noteColor.intValue = event.color
                    savedStateHandle["color"] = event.color // persist to SavedStateHandle
                }
                is AddEditNoteEvent.SaveNote -> {
                    viewModelScope.launch {
                        try {
                            noteUseCases.addNote(
                                Note(
                                    title = noteTitle.value.text,
                                    content = noteContent.value.text,
                                    color = noteColor.value,
                                    timestamp = System.currentTimeMillis(),
                                    id = currentNoteId,
                                ),
                            )
                            _eventFlow.emit(UiEvent.SavedNote)
                        } catch (e: InvalidNoteException) {
                            _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    message = e.message ?: "Couldn't save note",
                                ),
                            )
                        }
                    }
                }
                is AddEditNoteEvent.changeContentFocus -> {
                    _noteContent.value =
                        _noteContent.value.copy(
                            isHintVisible = !event.focusState.isFocused && _noteContent.value.text.isBlank(),
                        )
                }
            }
        }

        sealed class UiEvent {
            data class ShowSnackbar(
                val message: String,
            ) : UiEvent()

            object SavedNote : UiEvent()
        }
    }
