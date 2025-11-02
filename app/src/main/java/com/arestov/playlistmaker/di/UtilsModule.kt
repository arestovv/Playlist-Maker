package com.arestov.playlistmaker.di

import com.arestov.playlistmaker.utils.ResourceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilsModule = module {

    single<ResourceManager> {
        ResourceManager( androidContext())
    }
}