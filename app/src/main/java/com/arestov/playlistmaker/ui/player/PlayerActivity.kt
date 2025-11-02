package com.arestov.playlistmaker.ui.player

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.databinding.ActivityPlayerBinding
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.ui.media.MediaViewModel
import com.arestov.playlistmaker.utils.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayerActivity : AppCompatActivity() {
    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = viewModel.getTrack()

        //Слушатель состояние плеера
        viewModel.playerStateLiveData.observe(this) { state ->
            //Обновление таймера
            binding.textTimer.text = state.progress
            //Получаем состояние плеера (Playing = true, else false)
            val isPlaying = state is PlayerState.Playing
            //Смена иконки кнопки Play/Pause
            changeButtonState(isPlaying = isPlaying)
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
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
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