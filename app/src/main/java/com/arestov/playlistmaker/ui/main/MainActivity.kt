package com.arestov.playlistmaker.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arestov.playlistmaker.databinding.ActivityMainBinding
import com.arestov.playlistmaker.utils.ScreensHolder
import com.arestov.playlistmaker.utils.ScreensHolder.Screens.*
import com.arestov.playlistmaker.utils.ThemeManager

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val SWITCHER_DARK_THEME_STATE_KEY = "switcher_dark_theme_state_key"
const val TRACK_HISTORY_KEY = "track_history"
lateinit var sharedPrefs: SharedPreferences

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this, MainViewModel.factory(
                context = this,
                sharedPreferences = sharedPrefs)
        )[MainViewModel::class.java]
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        setContentView(binding.root)

        ThemeManager.setDarkMode(viewModel.getStateDarkMode())

        //Search clickListener
        binding.buttonSearch.setOnClickListener {
            ScreensHolder.launch(screen = SEARCH, context = this)
        }

        //Media clickListener
        binding.buttonMedia.setOnClickListener {
            ScreensHolder.launch(screen = MEDIA, context = this)
        }

        //Settings clickListener
        binding.buttonSetting.setOnClickListener {
            ScreensHolder.launch(screen = SETTINGS, context = this)
        }
    }

}