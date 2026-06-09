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

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.opennotes.feature_node.domain.model.Note
import com.opennotes.feature_node.domain.use_case.NoteUseCases
import com.opennotes.feature_node.presentation.MainActivity

@Composable
fun ZeroState(widgetId: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surfaceVariant)
            .cornerRadius(16.dp)
            .clickable(actionStartActivity<NotesWidgetConfigActivity>())
    ) {
        Text(
            text = "Tap to select a note",
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
fun SelectedNote(
    note: Note,
    noteUseCases: NoteUseCases,
    widgetId: Int
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .cornerRadius(16.dp)
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Text(
            text = note.title,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        Text(
            text = note.content,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 14.sp
            )
        )
    }
}