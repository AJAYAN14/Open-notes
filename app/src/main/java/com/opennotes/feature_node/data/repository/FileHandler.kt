package com.opennotes.feature_node.data.repository

import android.app.Application
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

interface FileHandler{
    suspend fun readTextFromUri(uri: Uri):String
    suspend fun saveToFile(filename:String,content:String):Uri?



}


class AndroidFileHandler(private val application: Application):FileHandler{
    override suspend fun readTextFromUri(uri:Uri):String=withContext(Dispatchers.IO){
        application.contentResolver.openInputStream(uri)?.use{ inputStream->
            BufferedReader(InputStreamReader(inputStream)).readText()

        } ?:throw IOException("Could not read from URI")
    }

    override suspend fun  saveToFile(filename:String,content:String):Uri?=withContext(Dispatchers.IO)
    {
        val notesDir= File(application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"notes_backup")
        if(!notesDir.exists()) notesDir.mkdirs()


        val file=File(notesDir,filename)
        file.writeText(content)
        return@withContext FileProvider.getUriForFile(
            application,
            "${application.packageName}.fileprovider",
            file
        )
    }
}