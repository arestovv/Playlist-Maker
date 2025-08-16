package com.arestov.playlistmaker.ui.player

import GetTrackHistoryUseCase
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.creator.Creator
import com.arestov.playlistmaker.presentation.MediaPlayerHelper
import com.arestov.playlistmaker.ui.main.sharedPrefs
import com.arestov.playlistmaker.utils.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar

class PlayerActivity : AppCompatActivity() {
    private lateinit var historyHolder: GetTrackHistoryUseCase
    lateinit var mediaPlayerHelper: MediaPlayerHelper
    private lateinit var buttonPlay: ImageView
    private lateinit var tvTimer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        //Get selected track
        historyHolder = Creator.provideGetTrackHistoryUseCase(sharedPrefs)
        val track = historyHolder.getTracks().first()
        mediaPlayerHelper = MediaPlayerHelper(track)

        //mock for add track favorite
        var trackIsFavorite = false
        //mock add to playlist
        var trackIsHasPlaylist = false

        val imageAlbum: ImageView = findViewById(R.id.image_album_player_screen)
        val tvTrackName: TextView = findViewById(R.id.track_name_player_screen)
        val tvArtistName: TextView = findViewById(R.id.artist_name_player_screen)

        buttonPlay = findViewById(R.id.button_play_player_screen)
        tvTimer = findViewById(R.id.text_timer_player_screen)
        val buttonAddToPlayList: ImageView = findViewById(R.id.button_add_to_playlist_player_screen)
        val buttonAddToFavorite: ImageView = findViewById(R.id.button_add_to_favorite_player_screen)

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
            finish()
        }

        //Set image album
        Glide.with(imageAlbum)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.im_album_placeholder)
            .transform(RoundedCorners(Converter.Companion.dpToPx(8f, this)))
            .into(imageAlbum)

        tvTimer.text = Converter.Companion.mmToSs(0)
        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvDurationValue.text = track.trackTimeSeconds
        tvGenre.text = track.primaryGenreName
        tvCountry.text = track.country

        prepareMediaPlayer()

        //Show album name
        if (track.collectionName.isNotEmpty()) {
            tvAlbumValue.text = track.collectionName
            tvAlbumGroup.visibility = View.VISIBLE
        } else tvAlbumGroup.visibility = View.GONE

        //Show year
        if (track.releaseYear.isNotEmpty()) {
            tvYearValue.text = track.releaseYear
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
            if (mediaPlayerHelper.isPlaying()) {
                pausePlayer()
            } else {
                startPlayer()
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

        //Listener for timer
        mediaPlayerHelper.setOnTimerTickListener(object : MediaPlayerHelper.OnTimerTickListener {
            override fun onTimerTick(formattedTime: String) {
                tvTimer.text = formattedTime
            }
        })

        // Set button play when track is completed
        mediaPlayerHelper.setOnPlaybackStateChangedListener(object :
            MediaPlayerHelper.OnPlaybackStateChangedListener {
            override fun onPlaybackCompleted() {
                runOnUiThread {
                    buttonPlay.setImageResource(R.drawable.ic_button_play) // ← ставим иконку play
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerHelper.release()
    }

    private fun prepareMediaPlayer() {
        mediaPlayerHelper.prepare()
        buttonPlay.setImageResource(R.drawable.ic_button_play)
        tvTimer.text = Converter.Companion.mmToSs(0)
    }

    private fun startPlayer() {
        mediaPlayerHelper.start()
        buttonPlay.setImageResource(R.drawable.ic_button_pause)
    }

    private fun pausePlayer() {
        mediaPlayerHelper.pause()
        buttonPlay.setImageResource(R.drawable.ic_button_play)
    }
}