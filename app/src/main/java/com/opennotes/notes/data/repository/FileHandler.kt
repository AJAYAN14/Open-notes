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

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

interface FileHandler {
    suspend fun readTextFromUri(uriString: String): String

    suspend fun saveToFile(
        filename: String,
        content: String,
    ): String?

    suspend fun writeTextToUri(
        uriString: String,
        content: String,
    ): Boolean

    suspend fun saveImageToInternalStorage(
        uriString: String,
    ): String?

    suspend fun importBackupFromZip(
        uriString: String,
    ): String?

    suspend fun exportBackupToZip(
        uriString: String,
        notesJson: String,
    ): Boolean
}

class AndroidFileHandler(
    private val application: Application,
) : FileHandler {
    override suspend fun readTextFromUri(uriString: String): String =
        withContext(Dispatchers.IO) {
            application.contentResolver.openInputStream(Uri.parse(uriString))?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            } ?: throw IOException("Could not read from URI")
        }

    override suspend fun saveToFile(
        filename: String,
        content: String,
    ): String? =
        withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = application.contentResolver

                val values =
                    ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, filename)
                        put(MediaStore.Downloads.MIME_TYPE, "application/json")
                        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                        put(MediaStore.Downloads.IS_PENDING, 1)
                    }

                val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val itemUri = resolver.insert(collection, values) ?: return@withContext null

                try {
                    resolver.openOutputStream(itemUri)?.use { outputStream ->
                        outputStream.write(content.toByteArray())
                    } ?: return@withContext null

                    values.clear()
                    values.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(itemUri, values, null, null)

                    return@withContext itemUri.toString()
                } catch (_: Exception) {
                    resolver.delete(itemUri, null, null)
                    return@withContext null
                }
            }

            @Suppress("DEPRECATION")
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
                return@withContext null
            }

            val file = File(downloadsDir, filename)
            return@withContext try {
                file.writeText(content)
                Uri.fromFile(file).toString()
            } catch (_: Exception) {
                null
            }
        }

    override suspend fun writeTextToUri(
        uriString: String,
        content: String,
    ): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext try {
                application.contentResolver.openOutputStream(Uri.parse(uriString))?.use { outputStream ->
                    outputStream.write(content.toByteArray())
                } ?: return@withContext false
                true
            } catch (_: Exception) {
                false
            }
        }

    override suspend fun saveImageToInternalStorage(uriString: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val imagesDir = File(application.filesDir, "images")
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }
                val filename = "img_${System.currentTimeMillis()}.jpg"
                val destFile = File(imagesDir, filename)

                application.contentResolver.openInputStream(Uri.parse(uriString))?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: return@withContext null

                "images/$filename"
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    override suspend fun importBackupFromZip(uriString: String): String? =
        withContext(Dispatchers.IO) {
            var notesJson: String? = null
            try {
                application.contentResolver.openInputStream(Uri.parse(uriString))?.use { inputStream ->
                    java.util.zip.ZipInputStream(inputStream).use { zis ->
                        var entry = zis.nextEntry
                        while (entry != null) {
                            if (entry.name == "notes.json") {
                                notesJson = zis.readBytes().toString(Charsets.UTF_8)
                            } else if (entry.name.startsWith("images/")) {
                                val destFile = File(application.filesDir, entry.name)
                                destFile.parentFile?.mkdirs()
                                destFile.outputStream().use { out ->
                                    zis.copyTo(out)
                                }
                            }
                            entry = zis.nextEntry
                        }
                    }
                }
                notesJson
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }

    override suspend fun exportBackupToZip(
        uriString: String,
        notesJson: String,
    ): Boolean =
        withContext(Dispatchers.IO) {
            try {
                application.contentResolver.openOutputStream(Uri.parse(uriString))?.use { outputStream ->
                    java.util.zip.ZipOutputStream(outputStream).use { zos ->
                        // Write notes.json
                        zos.putNextEntry(java.util.zip.ZipEntry("notes.json"))
                        zos.write(notesJson.toByteArray())
                        zos.closeEntry()

                        // Write images
                        val imagesDir = File(application.filesDir, "images")
                        if (imagesDir.exists() && imagesDir.isDirectory) {
                            imagesDir.listFiles()?.forEach { file ->
                                if (file.isFile) {
                                    zos.putNextEntry(java.util.zip.ZipEntry("images/${file.name}"))
                                    file.inputStream().use { input ->
                                        input.copyTo(zos)
                                    }
                                    zos.closeEntry()
                                }
                            }
                        }
                    }
                } ?: return@withContext false
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
}
