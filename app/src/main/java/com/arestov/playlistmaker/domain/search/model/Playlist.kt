package com.arestov.playlistmaker.domain.search.model

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val imageUri: String,
    val trackList: List<Long> = emptyList(),
    val trackCount: Int
)