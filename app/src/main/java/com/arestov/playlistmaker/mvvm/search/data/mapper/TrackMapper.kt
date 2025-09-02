package com.arestov.playlistmaker.mvvm.search.data.mapper

import com.arestov.playlistmaker.mvvm.search.data.dto.TrackDto
import com.arestov.playlistmaker.mvvm.search.domain.model.Track
import com.arestov.playlistmaker.utils.Converter

object TrackMapper {

    fun map(dto: TrackDto): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeSeconds = Converter.Companion.mmToSs(dto.trackTimeMillis),
            artworkUrl100 = dto.artworkUrl100,
            artworkUrl512 = dto.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"),
            collectionName = dto.collectionName,
            releaseYear = dto.releaseDate.split("-").first(),
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
    }

    fun mapList(dtos: List<TrackDto>): List<Track> {
        return dtos.map { map(it) }
    }
}