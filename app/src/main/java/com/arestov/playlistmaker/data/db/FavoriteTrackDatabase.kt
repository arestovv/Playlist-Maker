package com.arestov.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arestov.playlistmaker.data.db.dao.FavoriteTrackDao
import com.arestov.playlistmaker.data.db.entity.TrackEntity


@Database(version = 1, entities = [TrackEntity::class])
abstract class FavoriteTrackDatabase : RoomDatabase(){

    abstract fun trackDao(): FavoriteTrackDao
}