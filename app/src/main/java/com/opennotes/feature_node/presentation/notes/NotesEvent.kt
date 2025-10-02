package com.opennotes.feature_node.presentation.notes

import com.opennotes.feature_node.domain.model.Note


sealed class NotesEvent {

    data class DeleteNote(val note: Note):NotesEvent()
    object RestoreNote:NotesEvent()

    data class SearchNote(val query: String) : NotesEvent()

}