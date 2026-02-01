package com.arestov.playlistmaker.domain.interactor

import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getPlaylist(): Flow<List<Playlist>>
    suspend fun hasPlaylistTrack(playlistId: Int, trackId: Long): Boolean
    suspend fun addPlaylistTrack(playlistId: Int, track: Track)
    suspend fun deletePlaylistTrack(playlistId: Int, track: Track)
}