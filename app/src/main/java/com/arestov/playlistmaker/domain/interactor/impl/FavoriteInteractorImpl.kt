package com.arestov.playlistmaker.domain.interactor.impl

import com.arestov.playlistmaker.domain.interactor.FavoriteInteractor
import com.arestov.playlistmaker.domain.repository.FavoriteRepository
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(
    private val favoriteRepository: FavoriteRepository
) : FavoriteInteractor {
    override suspend fun addFavoriteTrack(track: Track) {
        favoriteRepository.addFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        favoriteRepository.deleteFavoriteTrack(track)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteRepository.getFavoriteTracks()
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return favoriteRepository.isFavorite(trackId)
    }
}