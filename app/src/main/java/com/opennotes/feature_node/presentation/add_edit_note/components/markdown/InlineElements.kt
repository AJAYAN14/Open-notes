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

package com.opennotes.feature_node.presentation.add_edit_note.components.markdown

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

interface TextStyleSegment {
    val delimiter: String

    fun getSpanStyle(): SpanStyle
}

data class BoldSegment(
    override val delimiter: String = "**",
) : TextStyleSegment {
    override fun getSpanStyle() = SpanStyle(fontWeight = FontWeight.Bold)
}

data class ItalicSegment(
    override val delimiter: String = "*",
) : TextStyleSegment {
    override fun getSpanStyle() = SpanStyle(fontStyle = FontStyle.Italic)
}

data class HighlightSegment(
    override val delimiter: String = "==",
) : TextStyleSegment {
    override fun getSpanStyle() = SpanStyle(background = Color.Yellow.copy(alpha = 0.2f))
}

data class Strikethrough(
    override val delimiter: String = "~~",
) : TextStyleSegment {
    override fun getSpanStyle() = SpanStyle(textDecoration = TextDecoration.LineThrough)
}

data class Underline(
    override val delimiter: String = "++",
) : TextStyleSegment {
    override fun getSpanStyle() = SpanStyle(textDecoration = TextDecoration.Underline)
}

data class CodeSegment(
    override val delimiter: String = "`",
) : TextStyleSegment {
    override fun getSpanStyle() =
        SpanStyle(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            background = Color.LightGray.copy(alpha = 0.3f),
        )
}
