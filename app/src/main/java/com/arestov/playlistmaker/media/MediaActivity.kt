package com.arestov.playlistmaker.media

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arestov.playlistmaker.PLAYLIST_MAKER_PREFERENCES
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.utils.ScreensHolder
import com.arestov.playlistmaker.utils.ScreensHolder.Screens.MEDIA
lateinit var sharedPrefs: SharedPreferences
class MediaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        ScreensHolder.saveCodeScreen(MEDIA, sharedPrefs)
    }

    override fun onResume() {
        super.onResume()
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        ScreensHolder.saveCodeScreen(MEDIA, sharedPrefs)    }
}