package com.arestov.playlistmaker.ui.main

import androidx.lifecycle.ViewModel
import com.arestov.playlistmaker.domain.repository.ThemeRepository

class MainViewModel(private val themeRepository: ThemeRepository) : ViewModel() {

    fun getStateDarkMode(): Boolean {
        return themeRepository.isDark()
    }
}