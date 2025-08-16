package com.arestov.playlistmaker.creator

import GetPreferencesStorageUseCase
import GetTrackHistoryUseCase
import GetTrackListUseCase
import android.content.SharedPreferences
import com.arestov.playlistmaker.data.network.TrackNetworkClient
import com.arestov.playlistmaker.data.network.TrackRetrofitNetworkClient
import com.arestov.playlistmaker.data.repository.PreferencesStorageImpl
import com.arestov.playlistmaker.data.repository.TrackHistoryRepositoryImpl
import com.arestov.playlistmaker.data.repository.TrackRepositoryImpl
import com.arestov.playlistmaker.domain.repository.PreferencesStorage
import com.arestov.playlistmaker.domain.repository.TrackHistoryRepository
import com.arestov.playlistmaker.domain.repository.TrackRepository

object Creator {
    fun provideGetTrackListUseCase(): GetTrackListUseCase {
        return GetTrackListUseCase(provideTrackRepository())
    }

    fun provideGetTrackHistoryUseCase(sharedPreferences: SharedPreferences): GetTrackHistoryUseCase {
            return GetTrackHistoryUseCase(provideTrackHistoryRepository(sharedPreferences))
    }

    fun provideGetPreferencesStorageUseCase(key: String, sharedPreferences: SharedPreferences): GetPreferencesStorageUseCase {
        return GetPreferencesStorageUseCase(providePreferencesStorage(key, sharedPreferences))
    }

    private fun providePreferencesStorage(key: String, sharedPreferences: SharedPreferences): PreferencesStorage {
        return PreferencesStorageImpl(key, sharedPreferences)
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
}