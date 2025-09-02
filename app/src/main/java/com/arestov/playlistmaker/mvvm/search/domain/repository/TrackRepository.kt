package com.arestov.playlistmaker.mvvm.search.domain.repository

import com.arestov.playlistmaker.mvvm.search.domain.entity.Resource
import com.arestov.playlistmaker.mvvm.search.domain.model.Track

interface TrackRepository {
    fun getTracks(text: String): Resource<List<Track>>
}