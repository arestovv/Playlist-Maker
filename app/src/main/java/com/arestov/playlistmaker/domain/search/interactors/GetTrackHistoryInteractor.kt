package com.arestov.playlistmaker.domain.search.interactors

import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.domain.search.repository.TrackHistoryRepository

class GetTrackHistoryInteractor(private val trackHistoryRepository: TrackHistoryRepository) {
    fun getTracks(): List<Track> = trackHistoryRepository.getTracks()
    fun addTrack(track: Track) = trackHistoryRepository.addTrack(track)
    fun hasTracks() = trackHistoryRepository.hasTracks()
    fun clear() = trackHistoryRepository.clear()
}