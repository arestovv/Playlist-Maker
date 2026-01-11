package com.arestov.playlistmaker.domain.repository

import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun addFavoriteTrack(track: Track)
    suspend fun deleteFavoriteTrack(track: Track)
    suspend fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun isFavorite(trackId: Long): Boolean
}