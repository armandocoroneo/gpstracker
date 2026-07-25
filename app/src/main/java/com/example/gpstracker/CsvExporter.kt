package com.example.gpstracker

import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {
    fun export(track: Track): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var result = "latitude,longitude,altitude,timestamp\n"
        for (p in track.points) {
            result += p.latitude.toString() + ","
            result += p.longitude.toString() + ","
            result += p.altitude.toString() + ","
            result += sdf.format(Date(p.timestamp)) + "\n"
        }
        return result
    }
}
