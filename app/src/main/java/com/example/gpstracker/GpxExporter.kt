package com.example.gpstracker

import java.text.SimpleDateFormat
import java.util.*

object GpxExporter {
    fun export(track: Track): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        var result = "<?xml version="1.0" encoding="UTF-8"?>
"
        result += "<gpx version="1.1" creator="GPSTracker" xmlns="http://www.topografix.com/GPX/1/1">
"
        result += "  <trk>
"
        result += "    <name>" + track.name + "</name>
"
        result += "    <trkseg>
"
        for (p in track.points) {
            val time = sdf.format(Date(p.timestamp))
            result += "      <trkpt lat="" + p.latitude + "" lon="" + p.longitude + "">
"
            result += "        <ele>" + p.altitude + "</ele>
"
            result += "        <time>" + time + "</time>
"
            result += "      </trkpt>
"
        }
        result += "    </trkseg>
"
        result += "  </trk>
"
        result += "</gpx>"
        return result
    }
}
