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

package com.opennotes.notes.data.repository

import com.opennotes.notes.domain.model.Note
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

interface JsonHandler {
    fun serializeNotes(notes: List<Note>): String

    fun parseToJsonArray(json: String): JsonArray
}

class KotlinxJsonHandler : JsonHandler {
    private val jsonFormat =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
        }

    override fun serializeNotes(notes: List<Note>): String = jsonFormat.encodeToString(notes)

    override fun parseToJsonArray(json: String): JsonArray = jsonFormat.parseToJsonElement(json).jsonArray
}
