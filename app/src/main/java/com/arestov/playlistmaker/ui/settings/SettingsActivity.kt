package com.arestov.playlistmaker.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arestov.playlistmaker.databinding.ActivitySettingsBinding
import com.arestov.playlistmaker.utils.ThemeManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsActivity : AppCompatActivity() {

  private val viewModel by viewModel<SettingsViewModel>()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Back
        binding.toolbarSettingsScreen.setNavigationOnClickListener {
            finish()
        }

        //Switch theme
        viewModel.themeStateLiveData.observe(this) { state ->
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
}