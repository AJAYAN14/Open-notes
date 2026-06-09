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

package com.opennotes.feature_node.widget


import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.hilt.navigation.compose.hiltViewModel
import com.opennotes.feature_node.domain.use_case.NoteUseCases
import com.opennotes.feature_node.presentation.settings.SettingsViewModel
import com.opennotes.ui.theme.OpenNotesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotesWidgetConfigActivity : ComponentActivity() {
    @Inject
    lateinit var noteUseCases: NoteUseCases

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setResult(RESULT_CANCELED)

        setContent {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val settings by settingsViewModel.settings.collectAsState()
            val notes by noteUseCases.getNotes().collectAsState(initial = null)
            val scope = rememberCoroutineScope()



            OpenNotesTheme(settings = settings) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Select a note") }
                        )
                    }
                ) { paddingValues ->
                    when {
                        notes == null -> {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(paddingValues),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        notes!!.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(paddingValues),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No notes found")
                            }
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            ) {
                                items(notes!!) { note ->
                                    Text(
                                        text = note.title.ifBlank { "Untitled" },
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                scope.launch {
                                                    val glanceId = GlanceAppWidgetManager(
                                                        applicationContext
                                                    )
                                                        .getGlanceIds(NotesWidget::class.java)
                                                        .firstOrNull {
                                                            GlanceAppWidgetManager(
                                                                applicationContext
                                                            )
                                                                .getAppWidgetId(it) == appWidgetId
                                                        }
                                                    glanceId?.let {
                                                        updateAppWidgetState(applicationContext,
                                                            PreferencesGlanceStateDefinition, it) { prefs ->
                                                            prefs.toMutablePreferences().apply {
                                                                this[intPreferencesKey("note_$appWidgetId")] = note.id!!
                                                            }
                                                        }
                                                        NotesWidget().update(applicationContext, it)
                                                    }
                                                    val resultValue = Intent().putExtra(
                                                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                                                        appWidgetId
                                                    )
                                                    setResult(RESULT_OK, resultValue)
                                                    finish()
                                                }
                                            }
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}