package com.arestov.playlistmaker.domain.interactor

import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylist(id: Int): Playlist
    suspend fun hasPlaylistTrack(playlistId: Int, trackId: Long): Boolean
    suspend fun addPlaylistTrack(playlistId: Int, track: Track)
    suspend fun deletePlaylistTrack(playlistId: Int, track: Track)
    suspend fun getPlaylistTracks(playlistId: Int): Flow<List<Track>>
    suspend fun getPlaylistTrack(trackId: Long): Track
}