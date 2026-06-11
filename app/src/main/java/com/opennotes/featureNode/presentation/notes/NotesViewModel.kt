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

package com.opennotes.featureNode.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opennotes.featureNode.domain.model.Note
import com.opennotes.featureNode.domain.usecase.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel
    @Inject
    constructor(
        private val noteUseCases: NoteUseCases,
        private val savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        private val _state = mutableStateOf(NotesState())
        val state: State<NotesState> = _state
        private var recentlyDeletedNote: Note? = null
        private var getNotesJob: Job? = null

    private val savedSortOrder = savedStateHandle
        .get<String>("sortOrder")
        ?.let { SortOrder.valueOf(it) }
        ?: SortOrder.DATE_CREATED_NEW

    init {
        getNotes(SortOrder.DATE_CREATED_NEW)
    }

        fun onEvent(event: NotesEvent) {
            when (event) {
                is NotesEvent.DeleteNote -> {
                    viewModelScope.launch {
                        noteUseCases.deleteNote(event.note)
                        recentlyDeletedNote = event.note
                    }
                }

                is NotesEvent.RestoreNote -> {
                    viewModelScope.launch {
                        noteUseCases.addNote(recentlyDeletedNote ?: return@launch)
                        recentlyDeletedNote = null
                    }
                }

                is NotesEvent.SearchNote -> {
                    _state.value = state.value.copy(searchQuery = event.query)
                    searchNotes(event.query)
                }

                is NotesEvent.SortNotes -> {
                    savedStateHandle["sortOrder"] = event.sortOrder.name
                    _state.value = state.value.copy(sortOrder = event.sortOrder)
                    getNotes(event.sortOrder)
                }


                else -> {}
            }
        }

    private fun getNotes(sortOrder: SortOrder) {
        getNotesJob?.cancel()
        getNotesJob = noteUseCases
            .getNotes()
            .onEach { notes ->
                val sorted = when (sortOrder) {
                    SortOrder.DATE_CREATED_NEW -> notes.sortedByDescending { it.timestamp }
                    SortOrder.DATE_CREATED_OLD -> notes.sortedBy { it.timestamp }
                    SortOrder.TITLE_A_Z -> notes.sortedBy { it.title.lowercase() }
                    SortOrder.TITLE_Z_A -> notes.sortedByDescending { it.title.lowercase() }
                }
                _state.value = state.value.copy(notes = sorted, sortOrder = sortOrder)
            }.launchIn(viewModelScope)
    }

        private fun searchNotes(query: String) {
            getNotesJob?.cancel()
            getNotesJob =
                noteUseCases
                    .searchNotes(query)
                    .onEach { notes ->
                        _state.value = state.value.copy(notes = notes)
                    }.launchIn(viewModelScope)
        }
    }
