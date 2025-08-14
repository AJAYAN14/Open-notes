package com.opennotes.feature_node.domain.use_case

import android.net.Uri
import com.google.gson.reflect.TypeToken
import com.opennotes.feature_node.data.repository.FileHandler
import com.opennotes.feature_node.data.repository.JsonHandler
import com.opennotes.feature_node.domain.model.Note
import com.opennotes.feature_node.domain.repository.NoteRepository
import com.opennotes.feature_node.domain.util.ImportResult

class ImportUseCases(
    private val repository: NoteRepository,
    private val fileHandler: FileHandler,
    private val jsonHandler: JsonHandler
) {
    suspend operator fun invoke(fileUri: Uri): ImportResult {
        return try {

            val notesJson = fileHandler.readTextFromUri(fileUri)


            val noteListType = object : TypeToken<List<Note>>() {}.type


            val notesToImport = jsonHandler.fromJson<List<Note>>(notesJson, noteListType)


            repository.insertNotes(notesToImport)

            ImportResult.Success
        } catch (e: Exception) {
            ImportResult.Error("Failed to import notes: ${e.localizedMessage}")
        }
    }
}