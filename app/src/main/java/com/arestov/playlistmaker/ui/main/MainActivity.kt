package com.arestov.playlistmaker.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arestov.playlistmaker.databinding.ActivityMainBinding
import com.arestov.playlistmaker.utils.ScreensHolder
import com.arestov.playlistmaker.utils.ScreensHolder.Screens.*
import com.arestov.playlistmaker.utils.ThemeManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val SWITCHER_DARK_THEME_STATE_KEY = "switcher_dark_theme_state_key"
const val TRACK_HISTORY_KEY = "track_history"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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