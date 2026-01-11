package com.arestov.playlistmaker.data.search.repository

import com.arestov.playlistmaker.data.search.mapper.TrackMapper
import com.arestov.playlistmaker.data.search.model.TracksResponse
import com.arestov.playlistmaker.data.search.network.TrackNetworkClient
import com.arestov.playlistmaker.domain.search.entity.Resource
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.domain.search.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val trackNetworkClient: TrackNetworkClient
) : TrackRepository {

    override fun getTracks(text: String): Flow<Resource<List<Track>>> = flow {
        val response = trackNetworkClient.getTracks(text)

        emit(
            if (response is TracksResponse) {
                val tracks = TrackMapper.mapList(response.results)
                Resource.Success(tracks)
            } else {
                Resource.Error("Network error")
            }
        )
    }
}