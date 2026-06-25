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

package com.opennotes.notes.presentation.addEditNote.components.markdown

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

enum class MarkdownFormat {
    BOLD,
    ITALIC,
    H1,
    H2,
    BULLET_LIST,
    NUMBERED_LIST,
    CHECKLIST,
    STRIKETHROUGH,
    QUOTE,
    CODE_BLOCK,
}

object MarkdownFormatter {
    fun injectMarkdown(
        format: MarkdownFormat,
        currentValue: TextFieldValue,
    ): TextFieldValue {
        val text = currentValue.text
        val selection = currentValue.selection

        return when (format) {
            MarkdownFormat.BOLD -> wrapSelection(text, selection, "**")
            MarkdownFormat.ITALIC -> wrapSelection(text, selection, "*")
            MarkdownFormat.STRIKETHROUGH -> wrapSelection(text, selection, "~~")
            MarkdownFormat.CODE_BLOCK -> wrapSelection(text, selection, "```\n", "\n```")
            MarkdownFormat.H1 -> injectPrefix(text, selection, "# ")
            MarkdownFormat.H2 -> injectPrefix(text, selection, "## ")
            MarkdownFormat.BULLET_LIST -> injectPrefix(text, selection, "- ")
            MarkdownFormat.NUMBERED_LIST -> injectNumberedListItem(text, selection)
            MarkdownFormat.CHECKLIST -> injectPrefix(text, selection, "[ ] ")
            MarkdownFormat.QUOTE -> injectPrefix(text, selection, "> ")
        }
    }

    private fun toggleOrInsertCheckbox(
        text: String,
        selection: TextRange,
    ): TextFieldValue {
        // Find the start and end of the current line
        var lineStart = text.lastIndexOf('\n', selection.min - 1)
        if (lineStart == -1) lineStart = 0 else lineStart += 1

        var lineEnd = text.indexOf('\n', selection.min)
        if (lineEnd == -1) lineEnd = text.length

        val currentLine = text.substring(lineStart, lineEnd)

        // Check if line already has a checkbox
        val uncheckedRegex = Regex("""^(\s*(?:[-*]\s*)?)(\[ ])(.*)""")
        val checkedRegex = Regex("""^(\s*(?:[-*]\s*)?)(\[[Xx]])(.*)""")

        val uncheckedMatch = uncheckedRegex.find(currentLine)
        val checkedMatch = checkedRegex.find(currentLine)

        return when {
            uncheckedMatch != null -> {
                // Toggle unchecked → checked
                val newLine = uncheckedMatch.groupValues[1] + "[X]" + uncheckedMatch.groupValues[3]
                val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
                TextFieldValue(text = newText, selection = TextRange(selection.min))
            }
            checkedMatch != null -> {
                // Toggle checked → unchecked
                val newLine = checkedMatch.groupValues[1] + "[ ]" + checkedMatch.groupValues[3]
                val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
                TextFieldValue(text = newText, selection = TextRange(selection.min))
            }
            else -> {
                // No checkbox on this line — insert a new one
                injectPrefix(text, selection, "[ ] ")
            }
        }
    }

    private fun wrapSelection(
        text: String,
        selection: TextRange,
        prefixWrapper: String,
        suffixWrapper: String = prefixWrapper,
    ): TextFieldValue {
        val start = selection.min
        val end = selection.max

        val selectedText = text.substring(start, end)
        val newText = text.substring(0, start) + prefixWrapper + selectedText + suffixWrapper + text.substring(end)

        val newCursorPos =
            if (start == end) {
                start + prefixWrapper.length
            } else {
                end + prefixWrapper.length + suffixWrapper.length
            }

        return TextFieldValue(
            text = newText,
            selection = TextRange(newCursorPos),
        )
    }

    private fun injectPrefix(
        text: String,
        selection: TextRange,
        prefix: String,
    ): TextFieldValue {
        // Find the start of the current line
        var lineStart = text.lastIndexOf('\n', selection.min - 1)
        if (lineStart == -1) lineStart = 0 else lineStart += 1

        val currentLineContent = text.substring(lineStart, selection.min)

        return if (currentLineContent.isBlank() && selection.min == lineStart) {
            // Cursor is at the start of an empty line — just add the prefix
            val newText = text.substring(0, lineStart) + prefix + text.substring(lineStart)
            TextFieldValue(
                text = newText,
                selection = TextRange(selection.min + prefix.length),
            )
        } else {
            // There's already content on this line — insert on a new line after the cursor
            val newText = text.substring(0, selection.min) + "\n" + prefix + text.substring(selection.min)
            TextFieldValue(
                text = newText,
                selection = TextRange(selection.min + 1 + prefix.length),
            )
        }
    }

    private fun injectNumberedListItem(
        text: String,
        selection: TextRange,
    ): TextFieldValue {
        val cursorPos = selection.min

        // Find start of the current line
        val lineStart = text.lastIndexOf('\n', cursorPos - 1).let { if (it == -1) 0 else it + 1 }
        val currentLine = text.substring(lineStart, cursorPos).trim()

        // If the current line is itself a numbered item, next should be +1
        val currentNumber =
            Regex("^(\\d+)\\.")
                .find(currentLine)
                ?.groupValues
                ?.get(1)
                ?.toIntOrNull()

        val nextNumber =
            if (currentNumber != null) {
                currentNumber + 1
            } else {
                // Scan backwards through all lines before cursor for the last numbered list item
                val lastNumbered =
                    text
                        .substring(0, lineStart)
                        .lines()
                        .lastOrNull { Regex("^\\d+\\.").containsMatchIn(it.trim()) }
                val lastNumber =
                    lastNumbered?.trim()?.let {
                        Regex("^(\\d+)\\.")
                            .find(it)
                            ?.groupValues
                            ?.get(1)
                            ?.toIntOrNull()
                    }
                (lastNumber ?: 0) + 1
            }

        return injectPrefix(text, selection, "$nextNumber. ")
    }
}
