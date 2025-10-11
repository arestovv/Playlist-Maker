package com.arestov.playlistmaker.domain.repository

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(state: Boolean)
}