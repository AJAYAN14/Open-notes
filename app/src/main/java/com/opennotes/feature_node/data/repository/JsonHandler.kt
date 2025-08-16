package com.opennotes.feature_node.data.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder

// In your data/repository directory

import java.lang.reflect.Type

interface JsonHandler {
    fun <T> toJson(data: T): String
    // The second parameter is now of type `Type`
    fun <T> fromJson(json: String, type: Type): T
}

class GsonJsonHandler : JsonHandler {
    private val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    override fun <T> toJson(data: T): String {
        return gson.toJson(data)
    }

    override fun <T> fromJson(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }
}