package com.opennotes.feature_node.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opennotes.feature_node.domain.model.Note
import com.opennotes.feature_node.domain.use_case.NoteUseCases
import com.opennotes.feature_node.domain.use_case.SearchNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotesViewModel @Inject constructor
        (
        private val noteUseCases: NoteUseCases
): ViewModel() {
        private val _state = mutableStateOf(NotesState())
        val state: State<NotesState> = _state
        private var recentlyDeletedNote: Note? = null
        private var getNotesJob: Job? = null

        init{
                getNotes()
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

                        else -> {}
                }
        }


        private fun getNotes() {
                getNotesJob?.cancel()
                getNotesJob = noteUseCases.getNotes()
                        .onEach { notes ->
                                _state.value = state.value.copy(notes = notes)
                        }
                        .launchIn(viewModelScope)
        }

        private fun searchNotes(query: String) {
                getNotesJob?.cancel()
                getNotesJob = noteUseCases.searchNotes(query)
                        .onEach { notes ->
                                _state.value = state.value.copy(notes = notes)
                        }
                        .launchIn(viewModelScope)
        }
}