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
        RootViewModel(themeRepository = get())
    }

    viewModel {
        MediaViewModel(
            favoriteRepository = get(),
            trackRepository = get(),
            historyInteractor = get()
        )
    }

    viewModel {
        PlayerViewModel(
            historyInteractor = get(),
            favoriteInteractor = get(),
            mediaPlayer = get()
        )
    }

    viewModel {
        SearchViewModel(
            getTrackListUseCase = get(),
            getTrackHistoryInteractor = get(),
            trackRepository = get()
        )
    }

    viewModel {
        SettingsViewModel(
            navigationInteractor = get(),
            themeInteractor = get(),
            resourceManager = get()
        )
    }
}