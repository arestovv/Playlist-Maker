package com.arestov.playlistmaker.mvvm.player.ui

import GetTrackHistoryUseCase
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.mvvm.creator.Creator
import com.arestov.playlistmaker.databinding.ActivityPlayerBinding
import com.arestov.playlistmaker.mvvm.search.domain.model.Track
import com.arestov.playlistmaker.mvvm.main.ui.sharedPrefs
import com.arestov.playlistmaker.utils.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PlayerActivity : AppCompatActivity() {
    private lateinit var historyHolder: GetTrackHistoryUseCase
    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get selected track
        historyHolder = Creator.provideGetTrackHistoryUseCase(sharedPrefs)
        val track = historyHolder.getTracks().first()

        viewModel = ViewModelProvider(this, PlayerViewModel.Companion.getFactory(track.previewUrl))
            .get(PlayerViewModel::class.java)

        //Observer state player
        viewModel.observePlayerState().observe(this) {
            changeButtonState(it == PlayerViewModel.Companion.STATE_PLAYING)
            enableButton(it != PlayerViewModel.Companion.STATE_DEFAULT)
        }

        //Observer progress track timer
        viewModel.observeProgressTime().observe(this) {
            binding.textTimer.text = it
        }

        //Back
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        //Set image album
        setImage(track)
        //Set track data
        binding.apply {
            textTimer.text = Converter.Companion.mmToSs(0)
            trackName.text = track.trackName
            artistName.text = track.artistName
            valueDuration.text = track.trackTimeSeconds
            valueGenre.text = track.primaryGenreName
            valueCountry.text = track.country
        }

        //Show album name
        if (track.collectionName.isNotEmpty()) {
            binding.valueAlbum.text = track.collectionName
            binding.groupAlbum.visibility = View.VISIBLE
        } else binding.groupAlbum.visibility = View.GONE

        //Show year
        if (track.releaseYear.isNotEmpty()) {
            binding.valueYear.text = track.releaseYear
            binding.groupYear.visibility = View.VISIBLE
        } else binding.groupYear.visibility = View.GONE

        //Play/pause track
        binding.buttonPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        //Listener for timer
        binding.textTimer.text = viewModel.observeProgressTime().value
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    private fun enableButton(isEnabled: Boolean) {
        binding.buttonPlay.isEnabled = isEnabled
    }

    private fun changeButtonState(isPlaying: Boolean) {
        if (isPlaying) {
            binding.buttonPlay.setImageResource(R.drawable.ic_button_pause)
        } else {
            binding.buttonPlay.setImageResource(R.drawable.ic_button_play)
        }
    }

    private fun setImage(track: Track) {
        Glide.with(binding.imageAlbum)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.im_album_placeholder)
            .transform(RoundedCorners(Converter.Companion.dpToPx(8f, this)))
            .into(binding.imageAlbum)
    }
}