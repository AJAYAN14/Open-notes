package com.opennotes.feature_node.domain.util

import android.net.Uri

sealed class ExportResult{
    data class  Success(val uri: Uri):ExportResult()
    data class Error(val message:String):ExportResult()



}


