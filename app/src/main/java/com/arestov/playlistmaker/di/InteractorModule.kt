package com.arestov.playlistmaker.di

import com.arestov.playlistmaker.domain.interactor.ExternalNavigationInteractor
import com.arestov.playlistmaker.domain.interactor.FavoriteInteractor
import com.arestov.playlistmaker.domain.interactor.ThemeInteractor
import com.arestov.playlistmaker.domain.interactor.impl.ExternalNavigationInteractorImpl
import com.arestov.playlistmaker.domain.interactor.impl.FavoriteInteractorImpl
import com.arestov.playlistmaker.domain.interactor.impl.ThemeInteractorImpl
import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import org.koin.dsl.module

val interactorModule = module {

    factory<ExternalNavigationInteractor> {
        ExternalNavigationInteractorImpl(repository = get())
    }

    single<GetTrackHistoryInteractor> {
        GetTrackHistoryInteractor(trackHistoryRepository = get())
    }

    single<ThemeInteractor> {
        ThemeInteractorImpl(themeRepository = get())
    }

    single<FavoriteInteractor> {
        FavoriteInteractorImpl(favoriteRepository = get())
    }
}