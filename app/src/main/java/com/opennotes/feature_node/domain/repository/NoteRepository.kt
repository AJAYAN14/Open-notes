package com.opennotes.feature_node.domain.repository


import com.opennotes.feature_node.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes():Flow<List<Note>>
    suspend fun getNoteById(id:Int): Note?
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(note: Note)
    fun searchNotes(query:String):Flow<List<Note>>
    suspend fun insertNotes(notes:List<Note>)
}