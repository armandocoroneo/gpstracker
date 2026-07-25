package com.example.gpstracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private var tracks: List<Track>,
    private val onClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tv_track_name)
        val meta: TextView = v.findViewById(R.id.tv_track_meta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = tracks[position]
        holder.name.text = t.name
        val min = t.durationSeconds / 60
        val sec = t.durationSeconds % 60
        holder.meta.text = "${t.date} %02d:%02d %.1f km ${t.points.size} pts".format(min, sec, t.distanceKm)
        holder.itemView.setOnClickListener { onClick(t) }
    }

    override fun getItemCount() = tracks.size

    fun update(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}
