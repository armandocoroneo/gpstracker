package com.example.gpstracker

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

class TrackerFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var chipStatus: Chip
    private lateinit var tvTime: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvPoints: TextView
    private lateinit var btnRecord: MaterialButton
    private lateinit var btnStop: MaterialButton
    private lateinit var btnSave: MaterialButton
    private lateinit var rvCoords: RecyclerView

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var trackStorage: TrackStorage

    private val points = mutableListOf<TrackPoint>()
    private var isRecording = false
    private var isPaused = false
    private var startTime = 0L
    private var elapsedBeforePause = 0L
    private var totalDistance = 0.0
    private var lastLocation: Location? = null
    private val polyline = Polyline()
    private var currentMarker: Marker? = null

    companion object {
        private const val LOC_PERM = 1001
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tracker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().userAgentValue = requireContext().packageName

        mapView = view.findViewById(R.id.map)
        chipStatus = view.findViewById(R.id.chip_gps_status)
        tvTime = view.findViewById(R.id.tv_time)
        tvDistance = view.findViewById(R.id.tv_distance)
        tvPoints = view.findViewById(R.id.tv_points)
        btnRecord = view.findViewById(R.id.btn_record)
        btnStop = view.findViewById(R.id.btn_stop)
        btnSave = view.findViewById(R.id.btn_save)
        rvCoords = view.findViewById(R.id.rv_coords)

        trackStorage = TrackStorage(requireContext())
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.overlays.add(polyline)

        rvCoords.layoutManager = LinearLayoutManager(requireContext())
        rvCoords.adapter = CoordAdapter(points)

        btnRecord.setOnClickListener { toggleRecording() }
        btnStop.setOnClickListener { stopRecording() }
        btnSave.setOnClickListener { saveTrack() }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { onNewLocation(it) }
            }
        }

        if (hasLocationPermission()) {
            centerOnCurrentLocation()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOC_PERM)
    }

    private fun centerOnCurrentLocation() {
        if (!hasLocationPermission()) return
        try {
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    mapView.controller.setZoom(16.0)
                    mapView.controller.setCenter(GeoPoint(it.latitude, it.longitude))
                }
            }
        } catch (_: SecurityException) {}
    }

    private fun toggleRecording() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return
        }
        if (!isRecording) {
            startRecording()
        } else if (!isPaused) {
            pauseRecording()
        } else {
            resumeRecording()
        }
    }

    private fun startRecording() {
        isRecording = true
        isPaused = false
        startTime = SystemClock.elapsedRealtime()
        elapsedBeforePause = 0L
        points.clear()
        totalDistance = 0.0
        lastLocation = null
        polyline.actualPoints.clear()
        currentMarker?.let { mapView.overlays.remove(it); currentMarker = null }

        chipStatus.text = getString(R.string.gps_on)
        chipStatus.setChipStrokeColorResource(R.color.positive)
        chipStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.positive))
        btnRecord.setIconResource(R.drawable.ic_pause)
        btnStop.isEnabled = true
        btnSave.isEnabled = false

        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build()
        try {
            fusedClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper())
        } catch (_: SecurityException) {}

        updateStats()
    }

    private fun pauseRecording() {
        isPaused = true
        elapsedBeforePause = SystemClock.elapsedRealtime() - startTime
        btnRecord.setIconResource(R.drawable.ic_play)
        try { fusedClient.removeLocationUpdates(locationCallback) } catch (_: SecurityException) {}
    }

    private fun resumeRecording() {
        isPaused = false
        startTime = SystemClock.elapsedRealtime() - elapsedBeforePause
        btnRecord.setIconResource(R.drawable.ic_pause)
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build()
        try {
            fusedClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper())
        } catch (_: SecurityException) {}
    }

    private fun stopRecording() {
        isRecording = false
        isPaused = false
        try { fusedClient.removeLocationUpdates(locationCallback) } catch (_: SecurityException) {}

        chipStatus.text = getString(R.string.gps_off)
        chipStatus.setChipStrokeColorResource(R.color.danger)
        chipStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.danger))
        btnRecord.setIconResource(R.drawable.ic_play)
        btnStop.isEnabled = false
        btnSave.isEnabled = points.isNotEmpty()
    }

    private fun onNewLocation(loc: Location) {
        val tp = TrackPoint(loc.latitude, loc.longitude, loc.altitude)
        points.add(tp)

        lastLocation?.let { prev ->
            totalDistance += prev.distanceTo(loc) / 1000.0
        }
        lastLocation = loc

        val geo = GeoPoint(loc.latitude, loc.longitude)
        polyline.actualPoints.add(geo)
        polyline.setPoints(polyline.actualPoints)

        currentMarker?.let { mapView.overlays.remove(it) }
        val marker = Marker(mapView)
        marker.position = geo
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        currentMarker = marker
        mapView.overlays.add(marker)
        mapView.controller.animateTo(geo)

        updateStats()
        rvCoords.adapter = CoordAdapter(points)
    }

    private fun updateStats() {
        val elapsed = if (isRecording && !isPaused) {
            SystemClock.elapsedRealtime() - startTime
        } else {
            elapsedBeforePause
        }
        val sec = (elapsed / 1000).toInt()
        val min = sec / 60
        val s = sec % 60
        tvTime.text = "%02d:%02d".format(min, s)
        tvDistance.text = "%.1f".format(totalDistance)
        tvPoints.text = points.size.toString()
    }

    private fun saveTrack() {
        if (points.isEmpty()) return
        val sec = ((SystemClock.elapsedRealtime() - startTime) / 1000).toInt()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val track = Track(
            name = "Recorrido " + (trackStorage.load().size + 1),
            date = sdf.format(Date()),
            durationSeconds = sec,
            distanceKm = totalDistance,
            points = points.toList()
        )
        trackStorage.add(track)
        Toast.makeText(requireContext(), R.string.track_saved, Toast.LENGTH_SHORT).show()

        points.clear()
        totalDistance = 0.0
        elapsedBeforePause = 0L
        polyline.actualPoints.clear()
        currentMarker?.let { mapView.overlays.remove(it); currentMarker = null }
        mapView.invalidate()
        updateStats()
        rvCoords.adapter = CoordAdapter(points)
        btnSave.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try { fusedClient.removeLocationUpdates(locationCallback) } catch (_: SecurityException) {}
    }
}
