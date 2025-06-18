package com.arestov.playlistmaker.search.track

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val data: List<Track>,
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = data[position]
        holder.bind(track)
        //listener track click
        holder.itemView.setOnClickListener {
            onItemClick(track)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}