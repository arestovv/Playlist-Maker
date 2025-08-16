package com.arestov.playlistmaker.app

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.arestov.playlistmaker.creator.Creator
import com.arestov.playlistmaker.domain.repository.PreferencesStorage
import com.arestov.playlistmaker.ui.main.SWITCHER_DARK_THEME_STATE_KEY

class App : Application() {
    lateinit var  preferencesStorage: PreferencesStorage

    override fun onCreate() {
        super.onCreate()
    }

    //Set theme and save to sharedPrefs
    fun setTheme(isDark: Boolean, sharedPrefs: SharedPreferences) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        //Save to sharedPrefs
        preferencesStorage = Creator.provideGetPreferencesStorageUseCase(
            SWITCHER_DARK_THEME_STATE_KEY, sharedPrefs)
        preferencesStorage.putBoolean(isDark)
    }

    //Get theme from sharedPrefs, if it first launch get theme from system
    fun getTheme(sharedPrefs: SharedPreferences): Boolean {
        preferencesStorage = Creator.provideGetPreferencesStorageUseCase(
            SWITCHER_DARK_THEME_STATE_KEY, sharedPrefs)
        //If sharedPrefs has saved theme
        return if (preferencesStorage.contains(SWITCHER_DARK_THEME_STATE_KEY)) {
            //Get theme sharedPrefs
            preferencesStorage.getBoolean(false)
        } else {
            //Get theme from system
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES
        }
    }
}