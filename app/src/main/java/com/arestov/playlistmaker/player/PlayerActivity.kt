package com.arestov.playlistmaker.player

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.arestov.playlistmaker.PLAYLIST_MAKER_PREFERENCES
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.search.TrackHistoryHolder
import com.arestov.playlistmaker.utils.Converter
import com.arestov.playlistmaker.utils.ScreensHolder
import com.arestov.playlistmaker.utils.ScreensHolder.Screens.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar

private lateinit var historyHolder: TrackHistoryHolder
lateinit var sharedPrefs: SharedPreferences

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        ScreensHolder.saveCodeScreen(PLAYER, sharedPrefs)
        historyHolder = TrackHistoryHolder(sharedPrefs)

        //mock for add track favorite
        var trackIsFavorite = false
        //mock for play track
        var trackIsPlay = false
        //mock add to playlist
        var trackIsHasPlaylist = false

        val imageAlbum: ImageView = findViewById(R.id.image_album_player_screen)
        val tvTrackName: TextView = findViewById(R.id.track_name_player_screen)
        val tvArtistName: TextView = findViewById(R.id.artist_name_player_screen)

        val buttonAddToPlayList: ImageView = findViewById(R.id.button_add_to_playlist_player_screen)
        val buttonPlay: ImageView = findViewById(R.id.button_play_player_screen)
        val buttonAddToFavorite: ImageView = findViewById(R.id.button_add_to_favorite_player_screen)
        val tvTimer: TextView = findViewById(R.id.text_timer_player_screen)

        val tvDurationValue: TextView = findViewById(R.id.value_duration_player_screen)
        val tvAlbumValue: TextView = findViewById(R.id.value_album_player_screen)
        val tvAlbumGroup: Group = findViewById(R.id.group_album_player_screen)
        val tvYearValue: TextView = findViewById(R.id.value_year_player_screen)
        val tvYearGroup: Group = findViewById(R.id.group_year_player_screen)
        val tvGenre: TextView = findViewById(R.id.value_genre_player_screen)
        val tvCountry: TextView = findViewById(R.id.value_country_player_screen)

        //Back
        val back = findViewById<MaterialToolbar>(R.id.toolbar_player_screen)
        back.setNavigationOnClickListener {
            ScreensHolder.saveCodeScreen(MAIN, sharedPrefs)
            finish()
        }

        //Get selected track
        val track = historyHolder.getFirstTrack()

        //Set image album
        Glide.with(imageAlbum)
            .load(track.getArtworkUrl512())
            .placeholder(R.drawable.im_album_placeholder)
            .transform(RoundedCorners(Converter.dpToPx(8f, this)))
            .into(imageAlbum)

        tvTimer.text = Converter.mmToSs(30000)
        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvDurationValue.text = Converter.mmToSs(track.trackTimeMillis)
        tvGenre.text = track.primaryGenreName
        tvCountry.text = track.country

        //Show album name
        if (!track.collectionName.isNullOrEmpty()) {
            tvAlbumValue.text = track.collectionName
            tvAlbumGroup.visibility = View.VISIBLE
        } else tvAlbumGroup.visibility = View.GONE

        //Show year
        if (!track.releaseDate.isNullOrEmpty()) {
            tvYearValue.text = track.releaseDate.split("-").first()
            tvYearGroup.visibility = View.VISIBLE
        } else tvYearGroup.visibility = View.GONE

        //Add track to favorite
        buttonAddToFavorite.setOnClickListener {
            if (trackIsFavorite) {
                buttonAddToFavorite.setImageResource(R.drawable.ic_button_add_to_favorite)
                trackIsFavorite = false
            } else {
                buttonAddToFavorite.setImageResource(R.drawable.ic_button_added_to_favorite)
                trackIsFavorite = true
            }
        }

        //Play/pause track
        buttonPlay.setOnClickListener {
            if (trackIsPlay) {
                buttonPlay.setImageResource(R.drawable.ic_button_play)
                trackIsPlay = false
            } else {
                buttonPlay.setImageResource(R.drawable.ic_button_pause)
                trackIsPlay = true
            }
        }

        //Add track to playlist
        buttonAddToPlayList.setOnClickListener {
            if (trackIsHasPlaylist) {
                buttonAddToPlayList.setImageResource(R.drawable.ic_button_add_to_playlist)
                trackIsHasPlaylist = false
            } else {
                buttonAddToPlayList.setImageResource(R.drawable.ic_button_added_to_playlist)
                trackIsHasPlaylist = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        ScreensHolder.saveCodeScreen(PLAYER, sharedPrefs)
    }
}