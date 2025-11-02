package com.arestov.playlistmaker.di

import com.arestov.playlistmaker.ui.main.MainViewModel
import com.arestov.playlistmaker.ui.media.MediaViewModel
import com.arestov.playlistmaker.ui.player.PlayerViewModel
import com.arestov.playlistmaker.ui.search.SearchViewModel
import com.arestov.playlistmaker.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        MediaViewModel()
    }

    viewModel {
        PlayerViewModel(get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get(), get())
    }
}