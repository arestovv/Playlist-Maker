package com.arestov.playlistmaker.ui.media

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arestov.playlistmaker.R

class MediaActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this, MediaViewModel.factory()
        )[MediaViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
    }
}