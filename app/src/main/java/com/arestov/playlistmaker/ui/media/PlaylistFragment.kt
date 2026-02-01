package com.arestov.playlistmaker.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.databinding.FragmentPlaylistBinding
import com.arestov.playlistmaker.domain.search.model.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlistAdapter: PlaylistAdapter

    private val viewModel: PlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.start()
        setupAdapters()
        observeViewModel()

        binding.createPlaylistButton.setOnClickListener {
            launchCreatePlaylistFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapters() {
        playlistAdapter = PlaylistAdapter(
            data = emptyList(),
            coroutineScope = viewLifecycleOwner.lifecycleScope
        ) { playlist ->
            //TODO клик по playlist
        }

        binding.playlistRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = playlistAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.screenStateLiveData.observe(getViewLifecycleOwner()) { state ->

            when (state) {
                is PlaylistScreenState.Content -> {
                    showPlaylist(state.playlists)
                }

                PlaylistScreenState.Empty -> {
                    showInfo()
                }
            }
        }
    }

    private fun showPlaylist(playlists: List<Playlist>) {
        playlistAdapter.updateData(playlists)
        binding.apply {
            playlistRecyclerView.isVisible = true
            infoContainerText.isGone = true
            infoContainerImage.isGone = true
        }
    }

    private fun showInfo() {
        binding.apply {
            infoContainerText.isVisible = true
            infoContainerImage.isVisible = true
            playlistRecyclerView.isGone = true
        }
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }

    private fun launchCreatePlaylistFragment() {
        findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
    }
}