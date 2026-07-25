package com.example.gpstracker

import java.text.SimpleDateFormat
import java.util.*

object GpxExporter {
    fun export(track: Track): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val sb = StringBuilder()
        sb.append("<?xml version="1.0" encoding="UTF-8"?>
")
        sb.append("<gpx version="1.1" creator="GPSTracker" xmlns="http://www.topografix.com/GPX/1/1">
")
        sb.append("  <trk>
")
        sb.append("    <name>").append(track.name).append("</name>
")
        sb.append("    <trkseg>
")
        track.points.forEach { p ->
            val time = sdf.format(Date(p.timestamp))
            sb.append("      <trkpt lat="").append(p.latitude).append("" lon="").append(p.longitude).append("">
")
            sb.append("        <ele>").append(p.altitude).append("</ele>
")
            sb.append("        <time>").append(time).append("</time>
")
            sb.append("      </trkpt>
")
        }
        sb.append("    </trkseg>
")
        sb.append("  </trk>
")
        sb.append("</gpx>")
        return sb.toString()
    }
}
