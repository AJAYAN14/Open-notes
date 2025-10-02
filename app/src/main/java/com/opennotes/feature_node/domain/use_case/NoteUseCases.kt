package com.opennotes.feature_node.domain.use_case

data class NoteUseCases(

    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote: GetNote,
    val searchNotes:SearchNotesUseCase,
    val importNotes:ImportUseCases,
    val exportNotes:ExportUseCases

)
