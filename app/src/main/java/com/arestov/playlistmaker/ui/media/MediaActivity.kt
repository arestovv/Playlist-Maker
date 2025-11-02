package com.arestov.playlistmaker.ui.media

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.ui.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MediaActivity : AppCompatActivity() {

    private val viewModel: MediaViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
    }
}