package com.arestov.playlistmaker.ui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class MediaViewModel() : ViewModel() {

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                MediaViewModel()
            }
        }
    }
}