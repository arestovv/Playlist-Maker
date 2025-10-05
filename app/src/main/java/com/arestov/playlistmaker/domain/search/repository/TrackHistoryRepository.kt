package com.arestov.playlistmaker.domain.search.repository

import com.arestov.playlistmaker.domain.search.model.Track

interface TrackHistoryRepository {
    fun getTracks(): List<Track>
    fun hasTracks(): Boolean
    fun addTrack(track: Track)
    fun clear()
}