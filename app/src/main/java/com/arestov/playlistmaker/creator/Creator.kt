//package com.arestov.playlistmaker.creator
//
//import android.content.Context
//import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
//import com.arestov.playlistmaker.domain.search.usecases.GetTrackListUseCase
//import android.content.SharedPreferences
//import com.arestov.playlistmaker.data.provider.SystemThemeProviderImpl
//import com.arestov.playlistmaker.data.repository.ExternalNavigationRepositoryImpl
//import com.arestov.playlistmaker.data.repository.PreferencesStorageRepositoryImpl
//import com.arestov.playlistmaker.data.repository.ThemeRepositoryImpl
//import com.arestov.playlistmaker.data.search.network.TrackNetworkClient
//import com.arestov.playlistmaker.data.search.network.TrackRetrofitNetworkClient
//import com.arestov.playlistmaker.data.search.repository.TrackHistoryRepositoryImpl
//import com.arestov.playlistmaker.data.search.repository.TrackRepositoryImpl
//import com.arestov.playlistmaker.domain.interactor.ExternalNavigationInteractor
//import com.arestov.playlistmaker.domain.interactor.ThemeInteractor
//import com.arestov.playlistmaker.domain.interactor.impl.ExternalNavigationInteractorImpl
//import com.arestov.playlistmaker.domain.interactor.impl.ThemeInteractorImpl
//import com.arestov.playlistmaker.domain.provider.SystemThemeProvider
//import com.arestov.playlistmaker.domain.repository.ExternalNavigationRepository
//import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository
//import com.arestov.playlistmaker.domain.repository.ThemeRepository
//import com.arestov.playlistmaker.domain.search.repository.TrackHistoryRepository
//import com.arestov.playlistmaker.domain.search.repository.TrackRepository
//import com.arestov.playlistmaker.ui.main.TRACK_HISTORY_KEY
//import com.arestov.playlistmaker.utils.ResourceManager
//
//object Creator {
//
//    // ------------------ Track ------------------
//    fun provideGetTrackListUseCase(): GetTrackListUseCase {
//        return GetTrackListUseCase(provideTrackRepository())
//    }
//
//    fun provideGetTrackHistoryUseCase(): GetTrackHistoryInteractor {
//        return GetTrackHistoryInteractor(provideTrackHistoryRepository())
//    }
//
//    private fun provideTrackHistoryRepository(): TrackHistoryRepository {
//        return TrackHistoryRepositoryImpl(
//            storage = providePreferencesStorageRepository(
//                key = TRACK_HISTORY_KEY,
//                sharedPreferences = sharedPrefs
//            )
//        )
//    }
//
//    private fun provideTrackRepository(): TrackRepository {
//        return TrackRepositoryImpl(provideTrackNetworkClient())
//    }
//
//    private fun provideTrackNetworkClient(): TrackNetworkClient {
//        return TrackRetrofitNetworkClient()
//    }
//
//    // ------------------ Settings ------------------
//    fun provideExternalNavigationRepository(context: Context): ExternalNavigationRepository {
//        return ExternalNavigationRepositoryImpl(context)
//    }
//
//    fun provideExternalNavigationInteractor(context: Context): ExternalNavigationInteractor {
//        return ExternalNavigationInteractorImpl(
//            repository = provideExternalNavigationRepository(context)
//        )
//    }
//
//    // ------------------ Preferences ------------------
//    fun providePreferencesStorageRepository(
//        key: String,
//        sharedPreferences: SharedPreferences
//    ): PreferencesStorageRepository {
//        return PreferencesStorageRepositoryImpl(key, sharedPreferences)
//    }
//
//    // ------------------ Managers ------------------
//    fun provideResourceManager(context: Context): ResourceManager {
//        return ResourceManager(context)
//    }
//
//    // ------------------ Theme ------------------
//
//    fun provideSystemThemeProvider(context: Context): SystemThemeProvider {
//        return SystemThemeProviderImpl(context = context)
//    }
//
//    fun provideThemeRepository(
//        context: Context, key: String, sharedPref: SharedPreferences
//    ): ThemeRepository {
//        return ThemeRepositoryImpl(
//            systemThemeProvider = provideSystemThemeProvider(context),
//            storage = providePreferencesStorageRepository(
//                key = key,
//                sharedPreferences = sharedPref
//            )
//        )
//    }
//
//    fun provideThemeInteractor(
//        context: Context, key: String, sharedPref: SharedPreferences
//    ): ThemeInteractor {
//        return ThemeInteractorImpl(
//            themeRepository = provideThemeRepository(context, key, sharedPref)
//        )
//    }
//}