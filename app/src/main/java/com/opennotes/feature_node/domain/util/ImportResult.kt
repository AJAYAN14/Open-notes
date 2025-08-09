package com.opennotes.feature_node.domain.util

sealed class ImportResult{
    object Success: ImportResult()
    data class Error(val message:String): ImportResult()
}