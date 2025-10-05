package com.arestov.playlistmaker.creator

import GetTrackHistoryUseCase
import GetTrackListUseCase
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.arestov.playlistmaker.data.repository.PreferencesStorageRepositoryImpl
import com.arestov.playlistmaker.ui.player.PlayerViewModel
import com.arestov.playlistmaker.data.search.network.TrackNetworkClient
import com.arestov.playlistmaker.data.search.network.TrackRetrofitNetworkClient
import com.arestov.playlistmaker.data.search.repository.TrackHistoryRepositoryImpl
import com.arestov.playlistmaker.data.search.repository.TrackRepositoryImpl
import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository
import com.arestov.playlistmaker.domain.search.repository.TrackHistoryRepository
import com.arestov.playlistmaker.domain.search.repository.TrackRepository

object Creator {

    // ------------------ Track ------------------
    fun provideGetTrackListUseCase(): GetTrackListUseCase {
        return GetTrackListUseCase(provideTrackRepository())
    }

    fun provideGetTrackHistoryUseCase(sharedPreferences: SharedPreferences): GetTrackHistoryUseCase {
        return GetTrackHistoryUseCase(provideTrackHistoryRepository(sharedPreferences))
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

    // ------------------ Player ------------------
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
}