package com.arestov.playlistmaker.domain.search.usecases

import com.arestov.playlistmaker.domain.search.entity.Resource
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.domain.search.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTrackListUseCase(private val trackRepository: TrackRepository) {

    fun execute(expression: String): Flow<Pair<List<Track>?, String?>> {
        return trackRepository.getTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}