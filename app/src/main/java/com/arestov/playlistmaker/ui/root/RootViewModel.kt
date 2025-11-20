package com.arestov.playlistmaker.ui.root

import androidx.lifecycle.ViewModel
import com.arestov.playlistmaker.domain.repository.ThemeRepository

class RootViewModel(private val themeRepository: ThemeRepository) : ViewModel() {

    fun getStateDarkMode(): Boolean {
        return themeRepository.isDark()
    }
}