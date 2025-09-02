package com.arestov.playlistmaker.mvvm.search.data.repository

import com.arestov.playlistmaker.mvvm.search.data.mapper.TrackMapper
import com.arestov.playlistmaker.mvvm.search.data.model.TracksResponse
import com.arestov.playlistmaker.mvvm.search.data.network.TrackNetworkClient
import com.arestov.playlistmaker.mvvm.search.domain.entity.Resource
import com.arestov.playlistmaker.mvvm.search.domain.model.Track
import com.arestov.playlistmaker.mvvm.search.domain.repository.TrackRepository

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