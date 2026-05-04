package com.arestov.playlistmaker.ui.search

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

class SearchFragment : Fragment() {

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
                    SearchScreen(
                        onTrackClick = {
                            findNavController()
                                .navigate(R.id.action_searchFragment_to_playerFragment)
                        }
                    )
                }
            }
        }
    }
}
