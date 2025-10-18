package com.arestov.playlistmaker.data.repository

import com.arestov.playlistmaker.domain.provider.SystemThemeProvider
import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository
import com.arestov.playlistmaker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(
    private val storage: PreferencesStorageRepository,
    private val systemThemeProvider: SystemThemeProvider
) : ThemeRepository {

    fun isAppDark(): Boolean {
        return storage.getBoolean()
    }

    fun isSet(): Boolean {
        return storage.contains()
    }

    fun isSystemDark(): Boolean {
        return systemThemeProvider.isSystemDarkThemeEnabled()
    }

    override fun setDark(state: Boolean) {
        storage.putBoolean(state)
    }

    override fun isDark(): Boolean {
        if (!isSet()) {
            val initialTheme = isSystemDark();
            setDark(initialTheme)
        }
        return isAppDark()
    }
}