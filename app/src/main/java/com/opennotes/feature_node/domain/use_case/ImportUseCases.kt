package com.opennotes.feature_node.domain.use_case

import android.net.Uri
import com.google.gson.reflect.TypeToken
import com.opennotes.feature_node.data.repository.FileHandler
import com.opennotes.feature_node.data.repository.JsonHandler // Correct import for JsonHandler
import com.opennotes.feature_node.domain.model.Note
import com.opennotes.feature_node.domain.repository.NoteRepository
import com.opennotes.feature_node.domain.util.ImportResult

class ImportUseCases(
    private val repository: NoteRepository,
    private val fileHandler: FileHandler,
    private val jsonHandler: JsonHandler // Correct class name
) {
    suspend operator fun invoke(fileUri: Uri): ImportResult {
        return try {
            // Fix 1: Call the method on the fileHandler object
            val notesJson = fileHandler.readTextFromUri(fileUri)

            // Fix 2: Use TypeToken to get the type of List<Note> for Gson
            val noteListType = object : TypeToken<List<Note>>() {}.type

            // Fix 3: Call the method on the jsonHandler object and pass the type
            val notesToImport = jsonHandler.fromJson<List<Note>>(notesJson, noteListType)

            // Fix 4: The repository method is likely insertNotes, not insertNote
            repository.insertNotes(notesToImport)

            ImportResult.Success
        } catch (e: Exception) {
            ImportResult.Error("Failed to import notes: ${e.localizedMessage}")
        }
    }
}