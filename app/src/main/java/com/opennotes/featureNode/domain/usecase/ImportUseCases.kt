/*
 *
 *  *  Copyright (c) 2026 Dhanush Sugganahalli <dhanush41230@gmail.com>
 *  *
 *  *  This program is free software; you can redistribute it and/or modify it under
 *  *  the terms of the GNU General Public License as published by the Free Software
 *  *  Foundation; either version 3 of the License, or (at your option) any later
 *  *  version.
 *  *
 *  *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  *  PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License along with
 *  *  this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.opennotes.featureNode.domain.usecase

import com.opennotes.featureNode.data.repository.FileHandler
import com.opennotes.featureNode.data.repository.JsonHandler
import com.opennotes.featureNode.domain.model.Note
import com.opennotes.featureNode.domain.repository.NoteRepository
import com.opennotes.featureNode.domain.util.ImportResult

class ImportUseCases(
    private val repository: NoteRepository,
    private val fileHandler: FileHandler,
    private val jsonHandler: JsonHandler,
) {
    suspend operator fun invoke(fileUriString: String): ImportResult {
        return try {
            val notesJson = fileHandler.readTextFromUri(fileUriString)

            if (notesJson.isBlank()) {
                return ImportResult.Error("File is empty")
            }

            // Deserialize with validation - catch invalid JSON first
            val rawNotes: List<Map<String, Any?>> =
                try {
                    jsonHandler.fromJsonToNoteMapList(notesJson)
                } catch (e: Exception) {
                    return ImportResult.Error("Invalid JSON format: ${e.message}")
                }

            if (rawNotes.isEmpty()) {
                return ImportResult.Error("No notes found in file")
            }

            // Validate and construct trusted Note objects
            val validNotes =
                rawNotes.mapNotNull { rawNote ->
                    val title = (rawNote["title"] as? String)?.trim()
                    val content = (rawNote["content"] as? String)?.trim()
                    val timestamp = (rawNote["timestamp"] as? Number)?.toLong()
                    val color = (rawNote["color"] as? Number)?.toInt()

                    // Only create Note if all required fields are present and valid
                    if ((title?.isNotBlank() == true || content?.isNotBlank() == true) &&
                        timestamp != null &&
                        color != null
                    ) {
                        Note(
                            title = title ?: "",
                            content = content ?: "",
                            timestamp = timestamp,
                            color = color,
                            id = null,
                        )
                    } else {
                        null // Skip invalid notes
                    }
                }

            if (validNotes.isEmpty()) {
                return ImportResult.Error("No valid notes found ")
            }

            repository.insertNotes(validNotes)

            ImportResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            ImportResult.Error("Failed to import notes: ${e.localizedMessage}")
        }
    }
}
