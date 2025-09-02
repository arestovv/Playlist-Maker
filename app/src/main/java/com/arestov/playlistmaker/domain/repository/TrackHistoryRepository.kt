package com.arestov.playlistmaker.domain.repository

import com.arestov.playlistmaker.domain.model.Track

interface TrackHistoryRepository {
    fun getTracks(): List<Track>
    fun hasTracks(): Boolean
    fun addTrack(track: Track)
    fun clear()
}