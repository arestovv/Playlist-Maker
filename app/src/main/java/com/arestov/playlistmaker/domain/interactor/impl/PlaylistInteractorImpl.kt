package com.arestov.playlistmaker.domain.interactor.impl

import com.arestov.playlistmaker.domain.interactor.PlaylistInteractor
import com.arestov.playlistmaker.domain.repository.PlaylistRepository
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun getPlaylist(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylist()
    }

    override suspend fun deletePlaylistTrack(playlistId: Int, track: Track) {
        playlistRepository.deletePlaylistTrack(playlistId, track)
    }

    override suspend fun addPlaylistTrack(playlistId: Int, track: Track) {
        playlistRepository.addPlaylistTrack(playlistId, track)

    }

    override suspend fun hasPlaylistTrack(playlistId: Int, trackId: Long): Boolean {
        return playlistRepository.hasPlaylistTrack(playlistId, trackId)
    }
}