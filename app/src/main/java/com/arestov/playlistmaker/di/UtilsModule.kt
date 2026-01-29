package com.arestov.playlistmaker.di

import com.arestov.playlistmaker.utils.ResourceManager
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilsModule = module {

    single<ResourceManager> {
        ResourceManager( context = androidContext())
    }

    factory { Gson() }
}