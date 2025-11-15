package com.arestov.playlistmaker.di

import com.arestov.playlistmaker.domain.interactor.ExternalNavigationInteractor
import com.arestov.playlistmaker.domain.interactor.ThemeInteractor
import com.arestov.playlistmaker.domain.interactor.impl.ExternalNavigationInteractorImpl
import com.arestov.playlistmaker.domain.interactor.impl.ThemeInteractorImpl
import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import org.koin.dsl.module

val interactorModule = module {

    factory<ExternalNavigationInteractor> {
        ExternalNavigationInteractorImpl(get())
    }

    single<GetTrackHistoryInteractor> {
        GetTrackHistoryInteractor(get())
    }

    single<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }

}