package com.arestov.playlistmaker

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.arestov.playlistmaker.app.App
import com.arestov.playlistmaker.utils.ScreensHolder
import com.arestov.playlistmaker.utils.ScreensHolder.Screens.*

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val SWITCHER_DARK_THEME_STATE_KEY = "switcher_dark_theme_state_key"
lateinit var sharedPrefs: SharedPreferences

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        //Restore choose theme from file preferences
        //Set theme
        val isDark = (applicationContext as App).getTheme(sharedPrefs)
        (applicationContext as App).setTheme(isDark, sharedPrefs)

        //Set main activity
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.button_search)
        val buttonMedia = findViewById<Button>(R.id.button_media)
        val buttonSettings = findViewById<Button>(R.id.button_setting)

        //Search clickListener
        buttonSearch.setOnClickListener {
            ScreensHolder.launch(SEARCH, this)
        }

        //Media clickListener
        buttonMedia.setOnClickListener {
            ScreensHolder.launch(MEDIA, this)
        }

        //Settings clickListener
        buttonSettings.setOnClickListener {
            ScreensHolder.launch(SETTINGS, this)
        }
    }
}