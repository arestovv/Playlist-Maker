package com.arestov.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.databinding.FragmentPlayerBinding
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.utils.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayerFragment : Fragment() {
    private val viewModel: PlayerViewModel by viewModel()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = viewModel.getTrack()

        //Слушатель состояние плеера
        viewModel.playerStateLiveData.observe(getViewLifecycleOwner()) { state ->
            //Обновление таймера
            binding.textTimer.text = state.progress
            //Получаем состояние плеера (Playing = true, else false)
            val isPlaying = state is PlayerState.Playing
            //Смена иконки кнопки Play/Pause
            changeButtonState(isPlaying = isPlaying)
        }

        //Back
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
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
            if (track.isFavorite) binding.buttonAddToFavorite.setImageResource(R.drawable.ic_button_added_to_favorite)
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

        //Favorite track listener
        viewModel.favoriteLiveData.observe(viewLifecycleOwner) { isFavorite ->
            if (isFavorite) {
                binding.buttonAddToFavorite.setImageResource(R.drawable.ic_button_added_to_favorite)
            } else {
                binding.buttonAddToFavorite.setImageResource(R.drawable.ic_button_add_to_favorite)
            }
        }

        binding.buttonAddToFavorite.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            .transform(RoundedCorners(Converter.Companion.dpToPx(8f, requireContext())))
            .into(binding.imageAlbum)
    }
}