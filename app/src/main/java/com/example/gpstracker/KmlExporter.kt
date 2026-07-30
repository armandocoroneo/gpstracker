package com.example.gpstracker

object KmlExporter {
    fun export(track: Track): String {
        var result = "<?xml version="1.0" encoding="UTF-8"?>
"
        result += "<kml xmlns="http://www.opengis.net/kml/2.2">
"
        result += "  <Document>
"
        result += "    <name>" + track.name + "</name>
"
        result += "    <Placemark>
"
        result += "      <name>" + track.name + "</name>
"
        result += "      <LineString>
"
        result += "        <coordinates>
"
        for (p in track.points) {
            result += "          " + p.longitude + "," + p.latitude + "," + p.altitude + "
"
        }
        result += "        </coordinates>
"
        result += "      </LineString>
"
        result += "    </Placemark>
"
        result += "  </Document>
"
        result += "</kml>"
        return result
    }
}
