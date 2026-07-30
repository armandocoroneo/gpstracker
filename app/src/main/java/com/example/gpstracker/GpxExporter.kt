package com.example.gpstracker

object GpxExporter {
    fun export(track: Track): String {
        var result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        result += "<gpx version=\"1.1\" creator=\"GPSTracker\">\n"
        result += "  <trk>\n"
        result += "    <name>" + track.name + "</name>\n"
        result += "    <trkseg>\n"
        for (p in track.points) {
            result += "      <trkpt lat=\"" + p.latitude + "\" lon=\"" + p.longitude + "\">\n"
            result += "        <ele>" + p.altitude + "</ele>\n"
            result += "        <time></time>\n"
            result += "      </trkpt>\n"
        }
        result += "    </trkseg>\n"
        result += "  </trk>\n"
        result += "</gpx>"
        return result
    }
}
