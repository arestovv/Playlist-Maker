package com.arestov.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.arestov.playlistmaker.data.db.entity.PlaylistEntity
import com.arestov.playlistmaker.domain.search.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist_table ORDER BY createdAt DESC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    fun getPlaylist(playlistId: Int): Flow<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(PlaylistEntityplaylistEntity: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)
}
