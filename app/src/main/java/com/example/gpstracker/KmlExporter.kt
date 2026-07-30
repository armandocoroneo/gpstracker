package com.example.gpstracker

object KmlExporter {
    fun export(track: Track): String {
        var result = """<?xml version="1.0" encoding="UTF-8"?>""" + "\n"
        result += """<kml xmlns="http://www.opengis.net/kml/2.2">""" + "\n"
        result += "  <Document>\n"
        result += "    <name>" + track.name + "</name>\n"
        result += "    <Placemark>\n"
        result += "      <name>" + track.name + "</name>\n"
        result += "      <LineString>\n"
        result += "        <coordinates>\n"
        for (p in track.points) {
            result += "          " + p.longitude + "," + p.latitude + "," + p.altitude + "\n"
        }
        result += "        </coordinates>\n"
        result += "      </LineString>\n"
        result += "    </Placemark>\n"
        result += "  </Document>\n"
        result += "</kml>"
        return result
    }
}
