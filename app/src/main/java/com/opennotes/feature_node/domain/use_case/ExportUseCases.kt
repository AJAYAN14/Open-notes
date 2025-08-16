package com.opennotes.feature_node.domain.use_case

import android.util.Log
import com.opennotes.feature_node.data.repository.FileHandler
import com.opennotes.feature_node.data.repository.GsonJsonHandler
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
    suspend operator fun invoke(): ExportResult {
        return try {
            val allNotes = repository.getAllNotes()
                .filter{it.isNotEmpty()}
                .first()



            val notesJson = jsonHandler.toJson(allNotes)


           val fileUri=fileHandler.saveToFile ("notes_backup.json", notesJson)


            if(fileUri==null){
                return ExportResult.Error("Could not save the file to a URI")
            }

            ExportResult.Success(fileUri)
        } catch (e: Exception) {
            ExportResult.Error("Failed to export notes: ${e.message}")
        }
    }
}