package com.arestov.playlistmaker.ui.media

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.bumptech.glide.Glide
import java.io.File

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = itemView.findViewById(R.id.playlist_name)
    private val count: TextView = itemView.findViewById(R.id.playlist_track_count)
    private val image: ImageView = itemView.findViewById(R.id.playlist_image)


    fun bind(playlist: Playlist) {
        val file = File(playlist.imageUri)
        Glide.with(image.context)
            .load(Uri.fromFile(file))
            .placeholder(R.drawable.ic_album_placeholder)
            .error(R.drawable.ic_album_placeholder)
            .centerCrop()
            .into(image)

        name.text = playlist.name

        count.text = itemView.context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.trackCount,
            playlist.trackCount
        )
    }
}