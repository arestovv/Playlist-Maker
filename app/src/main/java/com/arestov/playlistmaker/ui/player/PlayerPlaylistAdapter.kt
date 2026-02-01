package com.arestov.playlistmaker.ui.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.LifecycleCoroutineScope
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.utils.Debounce.debounce

class PlayerPlaylistAdapter(
    private var data: List<Playlist>,
    private val coroutineScope: LifecycleCoroutineScope,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlayerPlaylistViewHolder>() {

    private val clickDebounce: (Playlist) -> Unit = debounce(
        delayMillis = CLICK_DEBOUNCE_DELAY,
        coroutineScope = coroutineScope,
        useLastParam = false
    ) { playlist ->
        onItemClick(playlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.playlist_view, parent, false)
        return PlayerPlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerPlaylistViewHolder, position: Int) {
        val playlist = data[position]
        holder.bind(playlist)
        //listener playlist click
        holder.itemView.setOnClickListener {
            clickDebounce(playlist)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(data: List<Playlist>) {
        this.data = data
        notifyDataSetChanged()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}