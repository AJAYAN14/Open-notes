package com.opennotes.feature_node.presentation.notes


import com.opennotes.feature_node.domain.model.Note


data class NotesState(
    val notes:List<Note> = emptyList(),
    val searchQuery:String=""

)
