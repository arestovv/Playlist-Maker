package com.arestov.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arestov.playlistmaker.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(tracks: PlaylistTrackEntity)

    @Delete()
    suspend fun deleteTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_table WHERE trackId = :trackId")
    suspend fun getTrack(trackId: Long): PlaylistTrackEntity
}
