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

package com.opennotes.notes.presentation.addEditNote.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.opennotes.notes.presentation.addEditNote.components.markdown.MarkdownFormat

private data class ToolbarItem(
    val icon: ImageVector,
    val contentDescription: String,
    val format: MarkdownFormat,
)

private val toolbarItems =
    listOf(
        ToolbarItem(Icons.Rounded.FormatBold, "Bold", MarkdownFormat.BOLD),
        ToolbarItem(Icons.Rounded.FormatItalic, "Italic", MarkdownFormat.ITALIC),
        ToolbarItem(Icons.Rounded.StrikethroughS, "Strikethrough", MarkdownFormat.STRIKETHROUGH),
        ToolbarItem(Icons.Rounded.HMobiledata, "Heading 1", MarkdownFormat.H1),
        ToolbarItem(Icons.AutoMirrored.Rounded.FormatListBulleted, "Bullet List", MarkdownFormat.BULLET_LIST),
        ToolbarItem(Icons.Rounded.FormatListNumbered, "Numbered List", MarkdownFormat.NUMBERED_LIST),
        ToolbarItem(Icons.Rounded.CheckBox, "Checklist", MarkdownFormat.CHECKLIST),
        ToolbarItem(Icons.Rounded.FormatQuote, "Quote", MarkdownFormat.QUOTE),
        ToolbarItem(Icons.Rounded.Code, "Code Block", MarkdownFormat.CODE_BLOCK),
    )

@Composable
fun FormatToolbar(
    contentColor: Color,
    onFormatClick: (MarkdownFormat) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        toolbarItems.forEach { item ->
            IconButton(
                onClick = { onFormatClick(item.format) },
            ) {
                Icon(
                    item.icon,
                    contentDescription = item.contentDescription,
                    modifier = Modifier.size(22.dp),
                    tint = contentColor,
                )
            }
        }
    }
}
