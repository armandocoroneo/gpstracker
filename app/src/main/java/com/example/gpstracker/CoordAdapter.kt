package com.example.gpstracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CoordAdapter(private val points: List<TrackPoint>) :
    RecyclerView.Adapter<CoordAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val latlon: TextView = v.findViewById(R.id.tv_latlon)
        val index: TextView = v.findViewById(R.id.tv_index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_coord, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = points[points.size - 1 - position]
        holder.latlon.text = "%.4f, %.4f".format(p.latitude, p.longitude)
        holder.index.text = "#${points.size - position}"
    }

    override fun getItemCount() = minOf(points.size, 50)
}
