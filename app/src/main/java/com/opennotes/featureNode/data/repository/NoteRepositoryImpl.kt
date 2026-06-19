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

package com.opennotes.featureNode.data.repository

import com.opennotes.featureNode.data.datasource.NoteDao
import com.opennotes.featureNode.domain.model.Note
import com.opennotes.featureNode.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.opennotes.featureNode.data.datasource.NoteEntity
import com.opennotes.featureNode.data.datasource.toNoteEntity

class NoteRepositoryImpl(
    private val dao: NoteDao,
) : NoteRepository {
    override fun getAllNotes(): Flow<List<Note>> = dao.getNotes().map { entities -> entities.map { it.toNote() } }

    override suspend fun getNoteById(id: Int): Note? = dao.getNoteById(id)?.toNote()

    override suspend fun insertNote(note: Note) {
        dao.insertNote(note.toNoteEntity())
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note.toNoteEntity())
    }

    override fun searchNotes(query: String): Flow<List<Note>> = dao.searchNotes(query).map { entities -> entities.map { it.toNote() } }

    override suspend fun insertNotes(notes: List<Note>) {
        dao.insertAll(notes.map { it.toNoteEntity() })
    }
}
