package com.arestov.playlistmaker.data.converters

import com.arestov.playlistmaker.data.db.entity.PlaylistEntity
import com.arestov.playlistmaker.domain.search.model.Playlist

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imageUri = playlist.imageUri,
            trackList = playlist.trackList,
            trackCount = playlist.trackCount
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            imageUri = entity.imageUri,
            trackList = entity.trackList,
            trackCount = entity.trackCount
        )
    }
}