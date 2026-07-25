package com.example.gpstracker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class TrackStorage(context: Context) {
    private val file = File(context.filesDir, "tracks.json")
    private val gson = Gson()

    fun save(tracks: List<Track>) {
        file.writeText(gson.toJson(tracks))
    }

    fun load(): List<Track> {
        if (!file.exists()) return emptyList()
        return try {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(file.readText(), type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun add(track: Track) {
        val list = load().toMutableList()
        list.add(0, track)
        save(list)
    }

    fun delete(track: Track) {
        val list = load().toMutableList()
        list.removeAll { it.id == track.id }
        save(list)
    }
}
