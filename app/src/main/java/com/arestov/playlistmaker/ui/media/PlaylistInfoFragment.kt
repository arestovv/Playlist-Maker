package com.arestov.playlistmaker.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.ui.compose.theme.PlaylistMakerTheme

class PlaylistInfoFragment : Fragment() {

    private val args: PlaylistInfoFragmentArgs by navArgs()

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
                    PlaylistInfoScreen(
                        playlistId = args.playlistId,
                        onBack = { findNavController().navigateUp() },
                        onTrackClick = {
                            findNavController()
                                .navigate(R.id.action_playlistInfoFragment_to_playerFragment)
                        },
                        onEditClick = { id ->
                            val action = PlaylistInfoFragmentDirections
                                .actionPlaylistInfoFragmentToCreatePlaylistFragment(id)
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }
}
