package com.arestov.playlistmaker.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.LifecycleCoroutineScope
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.utils.Debounce.debounce

class TrackAdapter(
    private var data: List<Track>,
    private val coroutineScope: LifecycleCoroutineScope,
    private val onItemClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private val clickDebounce: (Track) -> Unit = debounce(
        delayMillis = CLICK_DEBOUNCE_DELAY,
        coroutineScope = coroutineScope,
        useLastParam = false
    ) { track ->
        onItemClick(track)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = data[position]
        holder.bind(track)
        //listener track click
        holder.itemView.setOnClickListener {
            clickDebounce(track)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(newTracks: List<Track>) {
        data = newTracks
        notifyDataSetChanged()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}