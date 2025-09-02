package com.arestov.playlistmaker.mvvm.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arestov.playlistmaker.mvvm.search.domain.model.Track

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