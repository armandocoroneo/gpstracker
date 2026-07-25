package com.example.gpstracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var storage: TrackStorage
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = view.findViewById(R.id.rv_tracks)
        emptyState = view.findViewById(R.id.empty_state)
        storage = TrackStorage(requireContext())
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = TrackAdapter(emptyList()) {}
        rv.adapter = adapter
        loadTracks()
    }

    override fun onResume() {
        super.onResume()
        loadTracks()
    }

    private fun loadTracks() {
        val tracks = storage.load()
        if (tracks.isEmpty()) {
            rv.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            adapter.update(tracks)
        }
    }
}
