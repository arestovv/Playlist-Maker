package com.arestov.playlistmaker.data.search.mapper

import com.arestov.playlistmaker.data.search.dto.TrackDto
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.utils.Converter

object TrackMapper {

    fun map(dto: TrackDto): Track? {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeSeconds = Converter.mmToSs(dto.trackTimeMillis),
            artworkUrl100 = dto.artworkUrl100,
            artworkUrl512 = dto.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
            collectionName = dto.collectionName,
            releaseYear = dto.releaseDate?.split("-")?.firstOrNull() ?: "",
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl.toString()
        )
    }

    fun mapList(dtos: List<TrackDto>): List<Track> {
        return dtos.mapNotNull { map(it) }
    }
}