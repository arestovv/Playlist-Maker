package com.arestov.playlistmaker.mvvm.search.data.model

import com.arestov.playlistmaker.mvvm.search.data.dto.TrackDto

class TracksResponse(
    val results: List<TrackDto>,
) : NetworkResponse() {}