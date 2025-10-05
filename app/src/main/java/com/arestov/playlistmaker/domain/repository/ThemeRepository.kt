package com.arestov.playlistmaker.domain.repository

interface ThemeRepository {
    fun isAppDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
    fun hasThemePreference(): Boolean
}