package com.arestov.playlistmaker.domain.search.repository

import com.arestov.playlistmaker.domain.search.entity.Resource
import com.arestov.playlistmaker.domain.search.model.Track

interface TrackRepository {
    fun getTracks(text: String): Resource<List<Track>>
}