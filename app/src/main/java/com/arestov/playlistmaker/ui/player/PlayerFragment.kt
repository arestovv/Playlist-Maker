package com.arestov.playlistmaker.ui.player

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.databinding.FragmentPlayerBinding
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.utils.Converter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayerFragment : Fragment() {
    private val viewModel: PlayerViewModel by viewModel()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlistAdapter: PlayerPlaylistAdapter
    private var toast: Toast? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


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

        playlistAdapter = PlayerPlaylistAdapter(
            data = emptyList(),
            coroutineScope = viewLifecycleOwner.lifecycleScope
        ) { playlist ->
            viewModel.addTrackToPlaylist(playlist, track)
        }


        //Слушатель состояние плеера
        viewModel.playerStateProgressLiveData.observe(getViewLifecycleOwner()) { state ->
            //Обновление таймера
            binding.textTimer.text = state.progress
            //Получаем состояние плеера (Playing = true, else false)
            val isPlaying = state is PlayerStateProgress.Playing
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

        binding.playlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
        }

        viewModel.stateScreenLiveData.observe(getViewLifecycleOwner()) { state ->

            when (state) {
                is PlayerScreenState.Content -> {
                    playlistAdapter.updateData(state.playlists)
                }

                is PlayerScreenState.Empty -> {
                }

                //Favorite track listener
                is PlayerScreenState.Favorite -> {
                    val isFavorite = state.isFavorite
                    if (isFavorite) {
                        binding.buttonAddToFavorite.setImageResource(R.drawable.ic_button_added_to_favorite)
                    } else {
                        binding.buttonAddToFavorite.setImageResource(R.drawable.ic_button_add_to_favorite)
                    }
                }

                is PlayerScreenState.TrackAddedToPlaylist -> {
                    if (state.isAdded) {
                        showToast(
                            getString(
                                R.string.added_to_playlist,
                                state.playlist.name
                            )
                        )
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                    } else {
                        showToast(
                            getString(
                                R.string.track_already_added_to_playlist,
                                state.playlist.name
                            )
                        )
                    }
                }
            }
        }

        binding.buttonAddToFavorite.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        binding.createPlaylistButton.setOnClickListener {
            val action = PlayerFragmentDirections.actionPlayerFragmentToCreatePlaylistFragment(-1)
            findNavController().navigate(action)
        }

        val bottomSheetContainer = binding.playlistsBottomSheet
        val overlay = binding.overlay
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            isFitToContents = false
            isHideable = true
            halfExpandedRatio = 0.5f
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val normalized = ((slideOffset + 1f) / 2f).coerceIn(0f, 1f)
                overlay.alpha = normalized * 0.8f
            }
        })

        // Открываем BottomSheet
        binding.buttonAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        // Закрываем BottomSheet кликом на overlay
        overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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
        binding.buttonPlay.setIsPlaying(isPlaying)
    }

    private fun setImage(track: Track) {
        Glide.with(binding.imageAlbum)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.im_album_placeholder)
            .transform(RoundedCorners(Converter.Companion.dpToPx(8f, requireContext())))
            .into(binding.imageAlbum)
    }


    private fun showToast(text: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val textView = layout.findViewById<TextView>(R.id.toast_text)
        textView.text = text

        toast?.cancel()

        toast = Toast(requireContext()).apply {
            duration = Toast.LENGTH_LONG
            view = layout
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        }
        toast?.show()
    }
}