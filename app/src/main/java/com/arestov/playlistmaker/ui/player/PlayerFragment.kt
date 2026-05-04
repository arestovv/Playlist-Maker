package com.arestov.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arestov.playlistmaker.ui.compose.theme.PlaylistMakerTheme

class PlayerFragment : Fragment() {

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
                    PlayerScreen(
                        onBack = { findNavController().navigateUp() },
                        onCreatePlaylistClick = {
                            val action = PlayerFragmentDirections
                                .actionPlayerFragmentToCreatePlaylistFragment(-1)
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }
}
