package com.arestov.playlistmaker.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.databinding.FragmentFavoriteBinding
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.ui.search.TrackAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoriteFragment : Fragment() {
    private lateinit var trackAdapter: TrackAdapter
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MediaViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        observeViewModel()

        // Загружаем треки каждый раз при открытии фрагмента
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeFavoriteTracks()
            }
        }
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(
            data = emptyList(),
            coroutineScope = viewLifecycleOwner.lifecycleScope
        ) { track ->
            viewModel.addHistoryTrack(track)
            launchPlayerFragment()
        }

        binding.favoriteRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.screenStateLiveData.observe(getViewLifecycleOwner()) { state ->
            when (state) {
                is FavoriteScreenState.Empty -> showEmpty()
                is FavoriteScreenState.Content -> showTracks(state.tracks)
            }
        }
    }

    private fun showEmpty() {
        binding.apply {
            infoContainerImage.isVisible = true
            infoContainerText.isVisible = true
            favoriteRecyclerView.isGone = true

        }
    }

    private fun showTracks(tracks: List<Track>) {
        viewModel.setFavorite(tracks)
        trackAdapter.updateData(tracks)
        binding.apply {
            infoContainerImage.isGone = true
            infoContainerText.isGone = true
            favoriteRecyclerView.isVisible = true
        }

    }

    private fun launchPlayerFragment() {
        findNavController().navigate(R.id.action_mediaFragment_to_playerFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }
}
