package com.arestov.playlistmaker.data.model

import com.arestov.playlistmaker.data.dto.TrackDto

class TracksResponse(
    val results: List<TrackDto>,
) : NetworkResponse() {}