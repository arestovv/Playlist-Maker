package com.arestov.playlistmaker.creator

import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import com.arestov.playlistmaker.domain.search.usecases.GetTrackListUseCase
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.arestov.playlistmaker.data.repository.PreferencesStorageRepositoryImpl
import com.arestov.playlistmaker.data.repository.ThemeRepositoryImpl
import com.arestov.playlistmaker.ui.player.PlayerViewModel
import com.arestov.playlistmaker.data.search.network.TrackNetworkClient
import com.arestov.playlistmaker.data.search.network.TrackRetrofitNetworkClient
import com.arestov.playlistmaker.data.search.repository.TrackHistoryRepositoryImpl
import com.arestov.playlistmaker.data.search.repository.TrackRepositoryImpl
import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository
import com.arestov.playlistmaker.domain.repository.ThemeRepository
import com.arestov.playlistmaker.domain.search.repository.TrackHistoryRepository
import com.arestov.playlistmaker.domain.search.repository.TrackRepository
import com.arestov.playlistmaker.domain.settings.SharingInteractor
import com.arestov.playlistmaker.ui.settings.SettingsViewModel

object Creator {

    // ------------------ Track ------------------
    fun provideGetTrackListUseCase(): GetTrackListUseCase {
        return GetTrackListUseCase(provideTrackRepository())
    }

    fun provideGetTrackHistoryUseCase(sharedPreferences: SharedPreferences): GetTrackHistoryInteractor {
        return GetTrackHistoryInteractor(provideTrackHistoryRepository(sharedPreferences))
    }

    private fun provideTrackHistoryRepository(sharedPreferences: SharedPreferences): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(sharedPreferences)
    }

    private fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(provideTrackNetworkClient())
    }

    private fun provideTrackNetworkClient(): TrackNetworkClient {
        return TrackRetrofitNetworkClient()
    }

    // ------------------ Preferences ------------------
    fun providePreferencesStorageRepository(
        key: String,
        sharedPreferences: SharedPreferences
    ): PreferencesStorageRepository {
        return PreferencesStorageRepositoryImpl(key, sharedPreferences)
    }

    // ------------------ Theme ------------------
    fun provideThemeRepository(
        key: String, sharedPref: SharedPreferences
    ): ThemeRepository {
        return ThemeRepositoryImpl(
            providePreferencesStorageRepository(key, sharedPref)
        )
    }

    // ------------------ Factory ------------------
    fun providePlayerViewModelFactory(
        trackUrl: String,
        mediaPlayer: MediaPlayer = MediaPlayer()
    ): ViewModelProvider.Factory {
        return viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl, mediaPlayer)
            }
        }
    }

    fun provideSettingsViewModelFactory(
        sharingInteractor: SharingInteractor
    ): ViewModelProvider.Factory {
        return viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor)
            }
        }
    }
}