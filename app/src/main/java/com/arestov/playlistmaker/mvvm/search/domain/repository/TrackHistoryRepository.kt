package com.arestov.playlistmaker.mvvm.search.domain.repository

import com.arestov.playlistmaker.mvvm.search.domain.model.Track

interface TrackHistoryRepository {
    fun getTracks(): List<Track>
    fun hasTracks(): Boolean
    fun addTrack(track: Track)
    fun clear()
}