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

package com.opennotes.notes.domain.usecase

import com.opennotes.notes.data.repository.FileHandler
import com.opennotes.notes.data.repository.JsonHandler
import com.opennotes.notes.domain.repository.NoteRepository
import com.opennotes.notes.domain.util.ExportResult
import kotlinx.coroutines.flow.first

class ExportUseCases(
    private val repository: NoteRepository,
    private val fileHandler: FileHandler,
    private val jsonHandler: JsonHandler,
) {
    suspend operator fun invoke(targetUriString: String): ExportResult {
        return try {
            val allNotes =
                repository
                    .getAllNotes()
                    .first()

            if (allNotes.isEmpty()) {
                return ExportResult.Error("No notes to export")
            }

            val notesJson = jsonHandler.serializeNotes(allNotes)

            val isSaved = fileHandler.exportBackupToZip(targetUriString, notesJson)

            if (!isSaved) {
                return ExportResult.Error("Could not save the file to the selected location")
            }

            ExportResult.Success(targetUriString)
        } catch (e: Exception) {
            e.printStackTrace()
            ExportResult.Error("Failed to export notes: ${e.localizedMessage}")
        }
    }
}
