package com.example.gpstracker

data class TrackPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

data class Track(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val date: String,
    val durationSeconds: Int,
    val distanceKm: Double,
    val points: List<TrackPoint>
)
