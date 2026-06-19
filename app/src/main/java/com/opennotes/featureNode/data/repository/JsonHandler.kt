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

package com.opennotes.featureNode.data.repository

import com.google.gson.GsonBuilder

// In your data/repository directory

interface JsonHandler {
    fun <T> toJson(data: T): String

    fun fromJsonToNoteMapList(json: String): List<Map<String, Any?>>
}

class GsonJsonHandler : JsonHandler {
    private val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    override fun <T> toJson(data: T): String = gson.toJson(data)

    override fun fromJsonToNoteMapList(json: String): List<Map<String, Any?>> {
        val noteListType = object : com.google.gson.reflect.TypeToken<List<Map<String, Any?>>>() {}.type
        return gson.fromJson(json, noteListType)
    }
}
