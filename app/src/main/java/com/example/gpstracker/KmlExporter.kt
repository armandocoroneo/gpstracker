package com.example.gpstracker

object KmlExporter {
    fun export(track: Track): String {
        val sb = StringBuilder()
        sb.append("<?xml version="1.0" encoding="UTF-8"?>
")
        sb.append("<kml xmlns="http://www.opengis.net/kml/2.2">
")
        sb.append("  <Document>
")
        sb.append("    <name>").append(track.name).append("</name>
")
        sb.append("    <Placemark>
")
        sb.append("      <name>").append(track.name).append("</name>
")
        sb.append("      <LineString>
")
        sb.append("        <coordinates>
")
        track.points.forEach { p ->
            sb.append("          ").append(p.longitude).append(",").append(p.latitude).append(",").append(p.altitude).append("
")
        }
        sb.append("        </coordinates>
")
        sb.append("      </LineString>
")
        sb.append("    </Placemark>
")
        sb.append("  </Document>
")
        sb.append("</kml>")
        return sb.toString()
    }
}
