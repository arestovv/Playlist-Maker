package com.arestov.playlistmaker.app

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.arestov.playlistmaker.creator.Creator
import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository
import com.arestov.playlistmaker.ui.main.SWITCHER_DARK_THEME_STATE_KEY

class App : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}