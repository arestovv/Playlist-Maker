package com.arestov.playlistmaker.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.arestov.playlistmaker.LAST_SCREEN_KEY
import com.arestov.playlistmaker.MainActivity
import com.arestov.playlistmaker.media.MediaActivity
import com.arestov.playlistmaker.player.PlayerActivity
import com.arestov.playlistmaker.search.SearchActivity
import com.arestov.playlistmaker.settings.SettingsActivity

class ScreensHolder {
    companion object {

        //Save last open screen to sharedPreferences
        fun saveCodeScreen(screen: Screens, sharedPreferences: SharedPreferences) {
            sharedPreferences.edit()
                .putInt(LAST_SCREEN_KEY, screen.get())
                .apply()
        }

        //launch screen
        fun launch(screen: Screens, context: Context,) {
            val intent = getIntent(screen, context)
            context.startActivity(intent)
        }

        //Intent for launch fun
        private fun getIntent(screen: Screens, context: Context): Intent {
            return when (screen) {
                Screens.MAIN -> Intent(context, MainActivity::class.java)
                Screens.SEARCH -> Intent(context, SearchActivity::class.java)
                Screens.MEDIA -> Intent(context, MediaActivity::class.java)
                Screens.SETTINGS -> Intent(context, SettingsActivity::class.java)
                Screens.PLAYER -> Intent(context, PlayerActivity::class.java)
            }
        }
    }

    //Code screen
    enum class Screens(private val num: Int) {
        MAIN(0),
        SEARCH(1),
        MEDIA(2),
        SETTINGS(3),
        PLAYER(4);

        fun get(): Int {
            return num
        }
    }
}