package com.arestov.playlistmaker.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arestov.playlistmaker.creator.Creator
import com.arestov.playlistmaker.databinding.ActivitySettingsBinding
import com.arestov.playlistmaker.domain.settings.ExternalNavigator
import com.arestov.playlistmaker.domain.settings.impl.SharingInteractorImpl
import com.arestov.playlistmaker.utils.ThemeManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    val navigator = ExternalNavigator(this)
    val sharingInteractor = SharingInteractorImpl(navigator, this)
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            Creator.provideSettingsViewModelFactory(sharingInteractor)
        ).get(SettingsViewModel::class.java)

        //Back
        binding.toolbarSettingsScreen.setNavigationOnClickListener {
            finish()
        }

        //Switch theme
        viewModel.themeStateLiveData.observe(this) { state ->
            binding.switcherTheme.isChecked = state
            ThemeManager.applyDarkTheme(enabled = state)
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