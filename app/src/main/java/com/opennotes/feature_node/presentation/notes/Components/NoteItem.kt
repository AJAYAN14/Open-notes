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

package com.opennotes.feature_node.presentation.notes.Components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.opennotes.feature_node.domain.model.Note
import com.opennotes.feature_node.presentation.add_edit_note.components.markdown.MarkdownText

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    onDeleteClick: () -> Unit,
    onNoteClick: () -> Unit = {},
) {
    val backgroundColor = Color(note.color)

    val textColor =
        remember(backgroundColor) {
            if (
                backgroundColor.contrastAgainst(Color.White) >=
                backgroundColor.contrastAgainst(Color.Black)
            ) {
                Color.White
            } else {
                Color.Black
            }
        }

    val borderColor =
        remember(backgroundColor) {
            if (textColor == Color.White) {
                Color.White.copy(alpha = 0.2f)
            } else {
                Color.Black.copy(alpha = 0.15f)
            }
        }

    Card(
        onClick = onNoteClick,
        modifier =
            modifier.border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius),
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                hoveredElevation = 4.dp,
            ),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .padding(bottom = 36.dp),
            ) {
                if (note.title.isNotBlank()) {
                    MarkdownText(
                        radius = cornerRadius.value.toInt(),
                        markdown = note.title,
                        isPreview = true,
                        isEnabled = true,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        spacing = 1.dp,
                        textColor = textColor,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (note.content.isNotBlank()) {
                    MarkdownText(
                        radius = cornerRadius.value.toInt(),
                        markdown = note.content,
                        isPreview = true,
                        isEnabled = true,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(max = 180.dp),
                        fontSize = 14.sp,
                        spacing = 1.dp,
                        textColor = textColor,
                    )
                }
            }
            IconButton(
                onClick = onDeleteClick,
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete note",
                    tint = textColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

private fun Color.contrastAgainst(other: Color): Float {
    val l1 = luminance() + 0.05f
    val l2 = other.luminance() + 0.05f
    return if (l1 > l2) l1 / l2 else l2 / l1
}
