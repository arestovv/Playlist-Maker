package com.arestov.playlistmaker.data.repository

import com.arestov.playlistmaker.data.mapper.TrackMapper
import com.arestov.playlistmaker.data.model.TracksResponse
import com.arestov.playlistmaker.data.network.TrackNetworkClient
import com.arestov.playlistmaker.domain.entity.Resource
import com.arestov.playlistmaker.domain.model.Track
import com.arestov.playlistmaker.domain.repository.TrackRepository

class TrackRepositoryImpl(
    private val trackNetworkClient: TrackNetworkClient
) : TrackRepository {

    override fun getTracks(text: String): Resource<List<Track>> {
        val response = trackNetworkClient.getTracks(text)

        return if (response is TracksResponse) {
            val tracks = TrackMapper.mapList(response.results)
            Resource.Success(tracks)
        } else {
            Resource.Error("Network error")
        }
    }
}