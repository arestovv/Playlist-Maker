package com.arestov.playlistmaker.di

import com.arestov.playlistmaker.ui.media.MediaViewModel
import com.arestov.playlistmaker.ui.player.PlayerViewModel
import com.arestov.playlistmaker.ui.root.RootViewModel
import com.arestov.playlistmaker.ui.search.SearchViewModel
import com.arestov.playlistmaker.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        RootViewModel(get())
    }

    viewModel {
        MediaViewModel(get(), get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get(), get())
    }
}