package com.arestov.playlistmaker.domain.search.model

data class Playlist(
    val id: Int,
    var name: String,
    var description: String,
    var imageUri: String,
    val trackList: List<Long> = emptyList(),
    val trackCount: Int
)