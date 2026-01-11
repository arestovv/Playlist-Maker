package com.arestov.playlistmaker.data.converters

import com.arestov.playlistmaker.data.db.entity.TrackEntity
import com.arestov.playlistmaker.domain.search.model.Track

class TrackDbConvertor {
    // Track -> TrackEntity
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeSeconds = track.trackTimeSeconds,
            artworkUrl100 = track.artworkUrl100,
            artworkUrl512 = track.artworkUrl512,
            collectionName = track.collectionName,
            releaseYear = track.releaseYear,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    // TrackEntity -> Track
    fun map(entity: TrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeSeconds = entity.trackTimeSeconds,
            artworkUrl100 = entity.artworkUrl100,
            artworkUrl512 = entity.artworkUrl512,
            collectionName = entity.collectionName,
            releaseYear = entity.releaseYear,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            previewUrl = entity.previewUrl
        )
    }
}