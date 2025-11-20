package com.arestov.playlistmaker.di

import android.content.SharedPreferences
import com.arestov.playlistmaker.data.repository.ExternalNavigationRepositoryImpl
import com.arestov.playlistmaker.data.repository.PreferencesStorageRepositoryImpl
import com.arestov.playlistmaker.data.repository.ThemeRepositoryImpl
import com.arestov.playlistmaker.data.search.repository.TrackHistoryRepositoryImpl
import com.arestov.playlistmaker.data.search.repository.TrackRepositoryImpl
import com.arestov.playlistmaker.domain.repository.ExternalNavigationRepository
import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository
import com.arestov.playlistmaker.domain.repository.ThemeRepository
import com.arestov.playlistmaker.domain.search.repository.TrackHistoryRepository
import com.arestov.playlistmaker.domain.search.repository.TrackRepository
import com.arestov.playlistmaker.ui.root.SWITCHER_DARK_THEME_STATE_KEY
import com.arestov.playlistmaker.ui.root.TRACK_HISTORY_KEY
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    // TrackHistoryRepository
    single<PreferencesStorageRepository>(named(TRACK_HISTORY_KEY)) {
        PreferencesStorageRepositoryImpl(TRACK_HISTORY_KEY, get<SharedPreferences>())
    }

    single<TrackHistoryRepository> {
        TrackHistoryRepositoryImpl(get(named(TRACK_HISTORY_KEY)), get())
    }

    // TrackRepository
    factory<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    // ExternalNavigationRepository
    single<ExternalNavigationRepository> {
        ExternalNavigationRepositoryImpl(androidContext())
    }

    // ThemeRepository
    single<PreferencesStorageRepository>(named(SWITCHER_DARK_THEME_STATE_KEY)) {
        PreferencesStorageRepositoryImpl(SWITCHER_DARK_THEME_STATE_KEY, get<SharedPreferences>())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get(named(SWITCHER_DARK_THEME_STATE_KEY)), get())
    }
}