package com.arestov.playlistmaker.domain.repository

import com.arestov.playlistmaker.domain.entity.Resource
import com.arestov.playlistmaker.domain.model.Track

interface TrackRepository {
    fun getTracks(text: String): Resource<List<Track>>
}