package com.arestov.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arestov.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: TrackEntity)

    @Delete()
    suspend fun deleteTracks(tracks: TrackEntity)

    @Query("SELECT * FROM track_table ORDER BY createdAt DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE trackId = :trackId)")
    suspend fun isFavorite(trackId: Long): Boolean
}
