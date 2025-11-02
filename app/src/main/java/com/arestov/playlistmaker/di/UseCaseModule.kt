package com.arestov.playlistmaker.di

import com.arestov.playlistmaker.domain.search.usecases.GetTrackListUseCase
import org.koin.dsl.module

val useCaseModule = module {

    single<GetTrackListUseCase> {
        GetTrackListUseCase(get())
    }
}