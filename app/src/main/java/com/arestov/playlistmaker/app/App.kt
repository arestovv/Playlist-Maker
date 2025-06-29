package com.arestov.playlistmaker.app

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.arestov.playlistmaker.SWITCHER_DARK_THEME_STATE_KEY

class App : Application() {

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
        sharedPrefs.edit()
            .putBoolean(SWITCHER_DARK_THEME_STATE_KEY, isDark)
            .apply()
    }

    //Get theme from sharedPrefs, if it first launch get theme from system
    fun getTheme(sharedPrefs: SharedPreferences): Boolean {
        //If sharedPrefs has saved theme
        return if (sharedPrefs.contains(SWITCHER_DARK_THEME_STATE_KEY)) {
            //Get theme sharedPrefs
            sharedPrefs.getBoolean(SWITCHER_DARK_THEME_STATE_KEY, false)
        } else {
            //Get theme from system
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES
        }
    }
}