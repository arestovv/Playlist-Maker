package com.arestov.playlistmaker.app

import android.app.Application
import com.arestov.playlistmaker.di.dataModule
import com.arestov.playlistmaker.di.interactorModule
import com.arestov.playlistmaker.di.mediaModule
import com.arestov.playlistmaker.di.repositoryModule
import com.arestov.playlistmaker.di.useCaseModule
import com.arestov.playlistmaker.di.utilsModule
import com.arestov.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                useCaseModule,
                viewModelModule,
                utilsModule,
                mediaModule
            )
        }
    }
}