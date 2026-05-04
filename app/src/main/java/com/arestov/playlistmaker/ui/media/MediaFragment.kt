package com.arestov.playlistmaker.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.ui.compose.theme.PlaylistMakerTheme

class MediaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                PlaylistMakerTheme {
                    MediaScreen(
                        onTrackClick = {
                            findNavController()
                                .navigate(R.id.action_mediaFragment_to_playerFragment)
                        },
                        onCreatePlaylistClick = {
                            val action = MediaFragmentDirections
                                .actionMediaFragmentToCreatePlaylistFragment(-1)
                            findNavController().navigate(action)
                        },
                        onPlaylistClick = { id ->
                            val action = MediaFragmentDirections
                                .actionMediaFragmentToPlaylistInfoFragment(id)
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }
}
