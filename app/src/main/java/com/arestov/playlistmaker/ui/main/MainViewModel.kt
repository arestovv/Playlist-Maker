package com.arestov.playlistmaker.ui.main

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.arestov.playlistmaker.creator.Creator.provideThemeRepository
import com.arestov.playlistmaker.domain.repository.ThemeRepository

class MainViewModel(private val themeRepository: ThemeRepository) : ViewModel() {

    fun getStateDarkMode(): Boolean {
        return themeRepository.isDark()
    }

    companion object {
        fun factory(
            context: Context,
            sharedPreferences: SharedPreferences
        ) = viewModelFactory {
            initializer {
                MainViewModel(
                    themeRepository = provideThemeRepository(
                        context = context,
                        key = SWITCHER_DARK_THEME_STATE_KEY,
                        sharedPref = sharedPreferences
                    )
                )
            }
        }
    }
}