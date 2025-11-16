package com.arestov.playlistmaker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.databinding.FragmentMainBinding
import com.arestov.playlistmaker.ui.media.MediaFragment
import com.arestov.playlistmaker.ui.search.SearchFragment
import com.arestov.playlistmaker.ui.settings.SettingsFragment
import com.arestov.playlistmaker.utils.ThemeManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val SWITCHER_DARK_THEME_STATE_KEY = "switcher_dark_theme_state_key"
const val TRACK_HISTORY_KEY = "track_history"

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ThemeManager.setDarkMode(viewModel.getStateDarkMode())

        //Search clickListener
        binding.buttonSearch.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(
                    R.id.rootFragmentContainerView,
                    SearchFragment.newInstance()
                )
                addToBackStack(SearchFragment.TAG)
            }
        }

        //Media clickListener
        binding.buttonMedia.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(
                    R.id.rootFragmentContainerView,
                    MediaFragment.newInstance()
                )
                addToBackStack(MediaFragment.TAG)
            }
        }

        //Settings clickListener
        binding.buttonSetting.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(
                    R.id.rootFragmentContainerView,
                    SettingsFragment.newInstance()
                )
                addToBackStack(SettingsFragment.TAG)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}