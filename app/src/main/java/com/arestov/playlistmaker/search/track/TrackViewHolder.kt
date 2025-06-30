package com.arestov.playlistmaker.search.track

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.utils.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
) {
    private val ivImage: ImageView = itemView.findViewById(R.id.track_image)
    private val tvName: TextView = itemView.findViewById(R.id.track_name)
    private val tvArtist: TextView = itemView.findViewById(R.id.track_artist)
    private val ivEllipse: ImageView = itemView.findViewById(R.id.track_ellipse)
    private val tvTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(item: Track) {
        Glide.with(ivImage)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_album_placeholder)
            .centerCrop()
            .transform(RoundedCorners(Converter.dpToPx(2f, parent.context)))
            .into(ivImage)

        tvName.text = item.trackName
        tvArtist.text = item.artistName
        ivEllipse.setImageResource(R.drawable.ic_ellipse)
        tvTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
    }
}