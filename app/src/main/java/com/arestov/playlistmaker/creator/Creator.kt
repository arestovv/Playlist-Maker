package com.arestov.playlistmaker.creator

import android.content.Context
import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import com.arestov.playlistmaker.domain.search.usecases.GetTrackListUseCase
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.arestov.playlistmaker.data.provider.SystemThemeProviderImpl
import com.arestov.playlistmaker.data.repository.ExternalNavigatorRepositoryImpl
import com.arestov.playlistmaker.data.repository.PreferencesStorageRepositoryImpl
import com.arestov.playlistmaker.data.repository.ThemeRepositoryImpl
import com.arestov.playlistmaker.ui.player.PlayerViewModel
import com.arestov.playlistmaker.data.search.network.TrackNetworkClient
import com.arestov.playlistmaker.data.search.network.TrackRetrofitNetworkClient
import com.arestov.playlistmaker.data.search.repository.TrackHistoryRepositoryImpl
import com.arestov.playlistmaker.data.search.repository.TrackRepositoryImpl
import com.arestov.playlistmaker.domain.repository.ExternalNavigatorRepository
import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository
import com.arestov.playlistmaker.domain.repository.ThemeRepository
import com.arestov.playlistmaker.domain.search.repository.TrackHistoryRepository
import com.arestov.playlistmaker.domain.search.repository.TrackRepository
import com.arestov.playlistmaker.ui.main.MainViewModel
import com.arestov.playlistmaker.ui.main.SWITCHER_DARK_THEME_STATE_KEY
import com.arestov.playlistmaker.ui.main.TRACK_HISTORY_KEY
import com.arestov.playlistmaker.ui.main.sharedPrefs
import com.arestov.playlistmaker.ui.search.SearchViewModel
import com.arestov.playlistmaker.ui.settings.SettingsViewModel
import com.arestov.playlistmaker.utils.ResourceManager

object Creator {

    // ------------------ Track ------------------
    fun provideGetTrackListUseCase(): GetTrackListUseCase {
        return GetTrackListUseCase(provideTrackRepository())
    }

    fun provideGetTrackHistoryUseCase(): GetTrackHistoryInteractor {
        return GetTrackHistoryInteractor(provideTrackHistoryRepository())
    }

    private fun provideTrackHistoryRepository(): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(
            storage = providePreferencesStorageRepository(
                key = TRACK_HISTORY_KEY,
                sharedPreferences = sharedPrefs
            )
        )
    }

    private fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(provideTrackNetworkClient())
    }

    private fun provideTrackNetworkClient(): TrackNetworkClient {
        return TrackRetrofitNetworkClient()
    }

    // ------------------ Settings ------------------
    fun provideExternalNavigationRepository(context: Context): ExternalNavigatorRepository {
        return ExternalNavigatorRepositoryImpl(context)
    }

    // ------------------ Preferences ------------------
    fun providePreferencesStorageRepository(
        key: String,
        sharedPreferences: SharedPreferences
    ): PreferencesStorageRepository {
        return PreferencesStorageRepositoryImpl(key, sharedPreferences)
    }

    // ------------------ Managers ------------------
    fun provideResourceManager(context: Context): ResourceManager {
        return ResourceManager(context)
    }

    // ------------------ Theme ------------------

    fun provideSystemThemeProviderImpl(context: Context): SystemThemeProviderImpl {
        return SystemThemeProviderImpl(context = context)
    }

    fun provideThemeRepository(
        context: Context,
        key: String,
        sharedPref: SharedPreferences
    ): ThemeRepository {
        return ThemeRepositoryImpl(
            systemThemeProvider = provideSystemThemeProviderImpl(context),
            storage = providePreferencesStorageRepository(
                key = key,
                sharedPreferences = sharedPref
            )
        )
    }

    // ------------------ ViewModelFactory ------------------

    fun provideMainViewModelFactory(
        context: Context,
        sharedPreferences: SharedPreferences
    ): ViewModelProvider.Factory {
        return viewModelFactory {
            initializer {
                MainViewModel(
                    themeRepository = provideThemeRepository(
                        context = context,
                        key = SWITCHER_DARK_THEME_STATE_KEY,
                        sharedPref = sharedPreferences
                    )
                )
            }
        }
    }

    fun providePlayerViewModelFactory(
    ): ViewModelProvider.Factory {
        return viewModelFactory {
            initializer {
                PlayerViewModel(
                    MediaPlayer(),
                    provideGetTrackHistoryUseCase()
                )
            }
        }
    }

    fun provideSettingsViewModelFactory(context: Context): ViewModelProvider.Factory {
        return viewModelFactory {
            initializer {
                SettingsViewModel(
                    externalNavigatorRepository = provideExternalNavigationRepository(context),
                    resourceManager = provideResourceManager(context),
                    themeRepository = provideThemeRepository(
                        context = context,
                        key = SWITCHER_DARK_THEME_STATE_KEY,
                        sharedPref = sharedPrefs
                    )
                )
            }
        }
    }

    fun provideSearchViewModelFactory(sharedPref: SharedPreferences): ViewModelProvider.Factory {
        return viewModelFactory {
            initializer {
                SearchViewModel(
                    getTrackListUseCase = provideGetTrackListUseCase(),
                    getTrackHistoryInteractor = provideGetTrackHistoryUseCase()
                )
            }
        }
    }
}