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

package com.opennotes.di

import android.app.Application

import com.opennotes.feature_node.data.data_source.NoteDatabase
import com.opennotes.feature_node.data.repository.AndroidFileHandler
import com.opennotes.feature_node.data.repository.FileHandler
import com.opennotes.feature_node.data.repository.GsonJsonHandler
import com.opennotes.feature_node.data.repository.JsonHandler
import com.opennotes.feature_node.data.repository.NoteRepositoryImpl
import com.opennotes.feature_node.domain.repository.NoteRepository
import com.opennotes.feature_node.domain.use_case.AddNote
import com.opennotes.feature_node.domain.use_case.DeleteNote
import com.opennotes.feature_node.domain.use_case.ExportUseCases
import com.opennotes.feature_node.domain.use_case.GetNote
import androidx.room.Room
import com.opennotes.feature_node.domain.use_case.GetNotes
import com.opennotes.feature_node.domain.use_case.ImportUseCases
import com.opennotes.feature_node.domain.use_case.NoteUseCases
import com.opennotes.feature_node.domain.use_case.SearchNotesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDataBase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    // NEW: Provides the FileHandler implementation
    @Provides
    @Singleton
    fun provideFileHandler(app: Application): FileHandler {
        return AndroidFileHandler(app)
    }

    // CORRECTED: Provides the concrete GsonJsonHandler implementation
    @Provides
    @Singleton
    fun provideJsonHandler(): JsonHandler {
        return GsonJsonHandler()
    }


    @Provides
    @Singleton
    fun provideNoteUseCaseId(
        repository: NoteRepository,
        jsonHandler: JsonHandler,
        fileHandler: FileHandler
    ): NoteUseCases {
        return NoteUseCases(

            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository),
            getNotes = GetNotes(repository),
            searchNotes = SearchNotesUseCase(repository),
            importNotes = ImportUseCases(repository, fileHandler,jsonHandler),
            exportNotes = ExportUseCases(repository, fileHandler,jsonHandler)
        )
    }
}