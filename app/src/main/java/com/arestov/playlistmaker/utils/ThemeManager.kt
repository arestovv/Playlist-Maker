package com.arestov.playlistmaker.utils

import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
        fun setDarkMode(state: Boolean) {
            val mode = when (state) {
                true -> AppCompatDelegate.MODE_NIGHT_YES
                false -> AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }
}
