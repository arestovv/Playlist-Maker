package com.arestov.playlistmaker.di

import com.arestov.playlistmaker.data.search.network.TrackRetrofitNetworkClient
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.arestov.playlistmaker.data.db.PlaylistDatabase
import com.arestov.playlistmaker.data.db.FavoriteTrackDatabase
import com.arestov.playlistmaker.data.db.PlaylistTrackDatabase
import com.arestov.playlistmaker.data.provider.SystemThemeProviderImpl
import com.arestov.playlistmaker.data.search.network.RetrofitClient
import com.arestov.playlistmaker.data.search.network.TrackNetworkClient
import com.arestov.playlistmaker.domain.provider.SystemThemeProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

val dataModule = module {

    single<SharedPreferences> {
        androidContext().getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    single<SystemThemeProvider> {
        SystemThemeProviderImpl(androidContext())
    }

    single {
        RetrofitClient.api
    }

    single<TrackNetworkClient> {
        TrackRetrofitNetworkClient(get())
    }

    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = FavoriteTrackDatabase::class.java,
            name = "track_database.db"
        ).build()
    }

    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = PlaylistDatabase::class.java,
            name = "playlist_database.db"
        ).build()
    }

    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = PlaylistTrackDatabase::class.java,
            name = "playlist_track_table.db"
        ).build()
    }
    single { get<FavoriteTrackDatabase>().trackDao() }
    single { get<PlaylistDatabase>().playlistDao() }
    single { get<PlaylistTrackDatabase>().playlistTrackDao() }

}