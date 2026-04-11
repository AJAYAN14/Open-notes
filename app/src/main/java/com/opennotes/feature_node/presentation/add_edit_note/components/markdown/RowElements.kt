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

sealed interface MarkdownElement {
    fun render(builder: StringBuilder)
}

data class Heading(val level: Int, val text: String) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("#".repeat(level)).append(" $text\n\n")
    }
}

data class CheckboxItem(val text: String, var checked: Boolean = false, var index: Int) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("[${if (checked) "X" else " "}] $text\n")
    }
}

data class Quote(val level: Int, val text: String) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("> ${text}\n")
    }
}

data class ListItem(
    val text: String,
    val isNumbered: Boolean = false,
    val number: Int? = null
) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        if (isNumbered && number != null) {
            builder.append("$number. $text\n")
        } else {
            builder.append("- $text\n")
        }
    }
}

data class CodeBlock(
    val code: String,
    val isEnded: Boolean = false,
    val firstLine: String,
    val language: String? = null
) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("```")
        language?.let { builder.append(it) }
        builder.append("\n$code")
        if (isEnded) {
            builder.append("```")
        }
        builder.append("\n")
    }
}

data class NormalText(val text: String) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("$text\n\n")
    }
}

data class ImageInsertion(val photoUri: String) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("!($photoUri)\n\n")
    }
}

data class Link(val fullText: String, val urlRanges: List<Pair<String, IntRange>>) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("$fullText\n\n")
    }
}

data class HorizontalRule(val fullText: String) : MarkdownElement {
    override fun render(builder: StringBuilder) {
        builder.append("---\n\n")
    }
}