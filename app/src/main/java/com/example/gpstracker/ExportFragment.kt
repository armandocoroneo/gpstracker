package com.example.gpstracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.io.File

class ExportFragment : Fragment() {

    private lateinit var rvTracks: RecyclerView
    private lateinit var rgFormat: RadioGroup
    private lateinit var tvPreview: TextView
    private lateinit var btnDownload: MaterialButton
    private lateinit var btnOpen: MaterialButton
    private lateinit var storage: TrackStorage

    private var selectedTrack: Track? = null
    private var selectedFormat = "gpx"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_export, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvTracks = view.findViewById(R.id.rv_export_tracks)
        rgFormat = view.findViewById(R.id.rg_format)
        tvPreview = view.findViewById(R.id.tv_preview)
        btnDownload = view.findViewById(R.id.btn_download)
        btnOpen = view.findViewById(R.id.btn_open)
        storage = TrackStorage(requireContext())

        rvTracks.layoutManager = LinearLayoutManager(requireContext())
        loadTracks()

        rgFormat.setOnCheckedChangeListener { _, id ->
            selectedFormat = when (id) {
                R.id.rb_kml -> "kml"
                R.id.rb_csv -> "csv"
                else -> "gpx"
            }
            updatePreview()
        }

        btnDownload.setOnClickListener { downloadFile() }
        btnOpen.setOnClickListener { openInMaps() }
    }

    override fun onResume() {
        super.onResume()
        loadTracks()
    }

    private fun loadTracks() {
        val tracks = storage.load()
        if (tracks.isEmpty()) {
            rvTracks.adapter = null
            tvPreview.text = "Primero guarda un track desde la pestania Tracker"
            btnDownload.isEnabled = false
            btnOpen.isEnabled = false
            return
        }
        val adapter = TrackAdapter(tracks) { track ->
            selectedTrack = track
            updatePreview()
            btnDownload.isEnabled = true
            btnOpen.isEnabled = true
        }
        rvTracks.adapter = adapter
        if (selectedTrack == null && tracks.isNotEmpty()) {
            selectedTrack = tracks[0]
            updatePreview()
            btnDownload.isEnabled = true
            btnOpen.isEnabled = true
        }
    }

    private fun generateContent(): String? {
        val track = selectedTrack ?: return null
        return when (selectedFormat) {
            "kml" -> KmlExporter.export(track)
            "csv" -> CsvExporter.export(track)
            else -> GpxExporter.export(track)
        }
    }

    private fun getMimeType(ext: String): String {
        return when (ext) {
            "gpx" -> "application/gpx+xml"
            "kml" -> "application/vnd.google-earth.kml+xml"
            else -> "text/csv"
        }
    }

    private fun createFile(): File? {
        val track = selectedTrack ?: return null
        val content = generateContent() ?: return null
        val ext = selectedFormat
        val fileName = track.name.replace(" ", "_") + "." + ext
        val file = File(requireContext().cacheDir, fileName)
        file.writeText(content)
        return file
    }

    private fun getUri(file: File): Uri {
        return FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".fileprovider",
            file
        )
    }

    private fun updatePreview() {
        val content = generateContent()
        tvPreview.text = content ?: "Selecciona un track"
    }

    private fun downloadFile() {
        val file = createFile() ?: return
        val uri = getUri(file)
        val ext = selectedFormat
        val fileName = file.name

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = getMimeType(ext)
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, fileName)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Compartir " + fileName))
        Toast.makeText(requireContext(), "Archivo generado: " + fileName, Toast.LENGTH_SHORT).show()
    }

    private fun openInMaps() {
        val file = createFile() ?: return
        val uri = getUri(file)
        val ext = selectedFormat
        val fileName = file.name

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, getMimeType(ext))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Verificar si hay alguna app que pueda abrirlo
        val pm = requireContext().packageManager
        if (intent.resolveActivity(pm) != null) {
            startActivity(Intent.createChooser(intent, "Abrir con..."))
        } else {
            // Si no hay app registrada para ese MIME type, probar con text/plain
            val fallback = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/plain")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (fallback.resolveActivity(pm) != null) {
                startActivity(Intent.createChooser(fallback, "Abrir con..."))
            } else {
                Toast.makeText(requireContext(), "No se encontro app compatible. Instala Google Earth, Strava o Wikiloc.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
