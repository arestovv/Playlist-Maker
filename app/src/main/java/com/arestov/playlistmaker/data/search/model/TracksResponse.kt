package com.arestov.playlistmaker.data.search.model

import com.arestov.playlistmaker.data.search.dto.TrackDto

class TracksResponse(
    val results: List<TrackDto>,
) : NetworkResponse() {}