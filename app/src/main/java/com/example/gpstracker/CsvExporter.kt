package com.example.gpstracker

object CsvExporter {
    fun export(track: Track): String {
        var result = "latitude,longitude,altitude\n"
        for (p in track.points) {
            result += p.latitude.toString() + "," + p.longitude.toString() + "," + p.altitude.toString() + "\n"
        }
        return result
    }
}
