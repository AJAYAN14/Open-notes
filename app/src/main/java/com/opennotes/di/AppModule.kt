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
import androidx.room.Room
import com.opennotes.featureNode.data.datasource.NoteDatabase
import com.opennotes.featureNode.data.repository.AndroidFileHandler
import com.opennotes.featureNode.data.repository.FileHandler
import com.opennotes.featureNode.data.repository.GsonJsonHandler
import com.opennotes.featureNode.data.repository.JsonHandler
import com.opennotes.featureNode.data.repository.NoteRepositoryImpl
import com.opennotes.featureNode.domain.repository.NoteRepository
import com.opennotes.featureNode.domain.usecase.AddNote
import com.opennotes.featureNode.domain.usecase.DeleteNote
import com.opennotes.featureNode.domain.usecase.ExportUseCases
import com.opennotes.featureNode.domain.usecase.GetNote
import com.opennotes.featureNode.domain.usecase.GetNotes
import com.opennotes.featureNode.domain.usecase.ImportUseCases
import com.opennotes.featureNode.domain.usecase.NoteUseCases
import com.opennotes.featureNode.domain.usecase.SearchNotesUseCase
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
    fun provideNoteDataBase(app: Application): NoteDatabase =
        Room
            .databaseBuilder(
                app,
                NoteDatabase::class.java,
                NoteDatabase.DATABASE_NAME,
            ).build()

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository = NoteRepositoryImpl(db.noteDao)

    // NEW: Provides the FileHandler implementation
    @Provides
    @Singleton
    fun provideFileHandler(app: Application): FileHandler = AndroidFileHandler(app)

    // CORRECTED: Provides the concrete GsonJsonHandler implementation
    @Provides
    @Singleton
    fun provideJsonHandler(): JsonHandler = GsonJsonHandler()

    @Provides
    @Singleton
    fun provideNoteUseCaseId(
        repository: NoteRepository,
        jsonHandler: JsonHandler,
        fileHandler: FileHandler,
    ): NoteUseCases =
        NoteUseCases(
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository),
            getNotes = GetNotes(repository),
            searchNotes = SearchNotesUseCase(repository),
            importNotes = ImportUseCases(repository, fileHandler, jsonHandler),
            exportNotes = ExportUseCases(repository, fileHandler, jsonHandler),
        )
}
