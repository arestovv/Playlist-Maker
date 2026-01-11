package com.arestov.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arestov.playlistmaker.data.db.dao.TrackDao
import com.arestov.playlistmaker.data.db.entity.TrackEntity


@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
}