package com.example.gpstracker

import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {
    fun export(track: Track): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("latitude,longitude,altitude,timestamp
")
        track.points.forEach { p ->
            sb.append(p.latitude).append(",")
            sb.append(p.longitude).append(",")
            sb.append(p.altitude).append(",")
            sb.append(sdf.format(Date(p.timestamp))).append("
")
        }
        return sb.toString()
    }
}
