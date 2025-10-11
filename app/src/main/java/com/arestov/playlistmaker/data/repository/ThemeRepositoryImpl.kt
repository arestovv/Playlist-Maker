package com.arestov.playlistmaker.data.repository

import com.arestov.playlistmaker.domain.provider.SystemThemeProvider
import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository
import com.arestov.playlistmaker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(
    private val storage: PreferencesStorageRepository,
    private val systemThemeProvider: SystemThemeProvider
) : ThemeRepository {

    fun isAppDarkThemeEnabled(): Boolean {
        return storage.getBoolean()
    }

    fun isThemeSet(): Boolean {
        return storage.contains()
    }

    fun isSystemDarkThemeEnabled(): Boolean {
        return systemThemeProvider.isSystemDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(state: Boolean) {
        storage.putBoolean(state)
    }

    override fun isDarkThemeEnabled(): Boolean {
        if (!isThemeSet()) {
            val initialTheme = isSystemDarkThemeEnabled();
            setDarkThemeEnabled(initialTheme)
        }
        return isAppDarkThemeEnabled()
    }
}