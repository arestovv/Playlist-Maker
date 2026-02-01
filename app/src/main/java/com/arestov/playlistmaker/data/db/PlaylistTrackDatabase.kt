package com.arestov.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arestov.playlistmaker.data.db.dao.PlaylistTrackDao
import com.arestov.playlistmaker.data.db.entity.PlaylistTrackEntity


@Database(version = 1, entities = [PlaylistTrackEntity::class])
abstract class PlaylistTrackDatabase : RoomDatabase(){

    abstract fun playlistTrackDao(): PlaylistTrackDao
}