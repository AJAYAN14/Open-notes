package com.opennotes.feature_node.domain.use_case

import com.opennotes.feature_node.data.repository.FileHandler
import com.opennotes.feature_node.data.repository.GsonJsonHandler

import com.opennotes.feature_node.domain.repository.NoteRepository
import com.opennotes.feature_node.domain.util.ExportResult

class ExportUseCases(
    private val repository: NoteRepository,
    private val fileHandler: FileHandler,
    private val jsonHandler:GsonJsonHandler
    ) {
    suspend operator fun invoke(): ExportResult {
        return try {
            // 1. Get all notes from the database
            val allNotes = repository.getNotes()

            // 2. Placeholder: Convert the list of notes to a JSON string
            val notesJson = jsonHandler.toJson(allNotes)


           val fileUri=fileHandler.saveToFile ("notes_backup.json", notesJson)

            ExportResult.Success
        } catch (e: Exception) {
            ExportResult.Error("Failed to export notes: ${e.message}")
        }
    }
}