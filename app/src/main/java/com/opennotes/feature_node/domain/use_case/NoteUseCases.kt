package com.opennotes.feature_node.domain.use_case

data class NoteUseCases(

    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote: GetNote,
    val getNotes:GetNotes,
    val searchNotes:SearchNotesUseCase,
    val importNotes:ImportUseCases,
    val exportNotes:ExportUseCases

)
