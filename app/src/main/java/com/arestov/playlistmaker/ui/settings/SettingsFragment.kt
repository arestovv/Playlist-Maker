package com.arestov.playlistmaker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arestov.playlistmaker.databinding.FragmentSettingsBinding
import com.arestov.playlistmaker.utils.ThemeManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Switch theme
        viewModel.themeStateLiveData.observe(getViewLifecycleOwner()) { state ->
            binding.switcherTheme.isChecked = state
            ThemeManager.setDarkMode(state = state)
        }

        binding.switcherTheme.setOnCheckedChangeListener { switcher, checked ->
            viewModel.setDarkThemeEnabled(checked)
        }

        //Share app
        binding.buttonShare.setOnClickListener {
            viewModel.shareApp()
        }

        //Write to support
        binding.buttonSupport.setOnClickListener {
            viewModel.openSupport()
        }

        //Agreement
        binding.buttonAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}