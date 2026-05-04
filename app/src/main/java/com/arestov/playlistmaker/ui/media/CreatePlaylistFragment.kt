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
import com.arestov.playlistmaker.ui.compose.theme.PlaylistMakerTheme

class CreatePlaylistFragment : Fragment() {

    private val args: CreatePlaylistFragmentArgs by navArgs()

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
                    CreatePlaylistScreen(
                        playlistId = args.playlistId,
                        onBack = { findNavController().navigateUp() }
                    )
                }
            }
        }
    }
}
