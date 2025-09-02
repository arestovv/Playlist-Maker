package com.arestov.playlistmaker.utils

import android.content.Context
import android.content.Intent
import com.arestov.playlistmaker.mvvm.main.ui.MainActivity
import com.arestov.playlistmaker.mvvm.media.ui.MediaActivity
import com.arestov.playlistmaker.mvvm.player.ui.PlayerActivity
import com.arestov.playlistmaker.mvvm.search.ui.SearchActivity
import com.arestov.playlistmaker.mvvm.settings.ui.SettingsActivity
import com.arestov.playlistmaker.utils.ScreensHolder.Screens.*

class ScreensHolder {
    companion object {

        //launch screen
        fun launch(screen: Screens, context: Context,) {
            val intent = getIntent(screen, context)
            context.startActivity(intent)
        }

        //Intent for launch fun
        private fun getIntent(screen: Screens, context: Context): Intent {
            return when (screen) {
                MAIN -> Intent(context, MainActivity::class.java)
                SEARCH -> Intent(context, SearchActivity::class.java)
                MEDIA -> Intent(context, MediaActivity::class.java)
                SETTINGS -> Intent(context, SettingsActivity::class.java)
                PLAYER -> Intent(context, PlayerActivity::class.java)
            }
        }
    }

    enum class Screens() {
        MAIN,
        SEARCH,
        MEDIA,
        SETTINGS,
        PLAYER
    }
}