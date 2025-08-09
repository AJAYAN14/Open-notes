package com.opennotes.feature_node.domain.util

sealed class ExportResult{
    object Success: ExportResult()
    data class Error(val message:String):ExportResult()



}


