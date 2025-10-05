package com.arestov.playlistmaker.data.repository

import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository
import com.arestov.playlistmaker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(val storage: PreferencesStorageRepository) : ThemeRepository {

    override fun isAppDarkThemeEnabled(): Boolean {
        return storage.getBoolean()
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        storage.putBoolean(enabled)
    }

    override fun hasThemePreference(): Boolean {
        return storage.contains()
    }
}