package com.arestov.playlistmaker.domain.search.repository

import com.arestov.playlistmaker.domain.search.entity.Resource
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getTracks(text: String): Flow<Resource<List<Track>>>
    suspend fun setFavorite(tracks: List<Track>)
}