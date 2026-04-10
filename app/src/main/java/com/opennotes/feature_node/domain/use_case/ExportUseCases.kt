package com.opennotes.feature_node.domain.use_case

import android.net.Uri
import com.opennotes.feature_node.data.repository.FileHandler
import com.opennotes.feature_node.data.repository.JsonHandler

import com.opennotes.feature_node.domain.repository.NoteRepository
import com.opennotes.feature_node.domain.util.ExportResult
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class ExportUseCases(
    private val repository: NoteRepository,
    private val fileHandler: FileHandler,
    private val jsonHandler: JsonHandler
    ) {
    suspend operator fun invoke(targetUri: Uri): ExportResult {
        return try {
            val allNotes = repository.getAllNotes()
                .filter{it.isNotEmpty()}
                .first()



            val notesJson = jsonHandler.toJson(allNotes)


            val isSaved = fileHandler.writeTextToUri(targetUri, notesJson)

            if(!isSaved){
                return ExportResult.Error("Could not save the file to the selected location")
            }

            ExportResult.Success(targetUri)
        } catch (e: Exception) {
            ExportResult.Error("Failed to export notes: ${e.message}")
        }
    }
}