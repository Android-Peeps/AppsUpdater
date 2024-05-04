package dev.mohancm.appsdownloader

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JsonParser {
    fun parseJson(jsonString: String): Map<String, App> {
        val gson = Gson()
        val type = object : TypeToken<Map<String, App>>() {}.type
        return gson.fromJson(jsonString, type)
    }
}