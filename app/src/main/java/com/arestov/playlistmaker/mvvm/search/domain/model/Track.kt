package com.arestov.playlistmaker.mvvm.search.domain.model

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeSeconds: String,
    val artworkUrl100: String,
    val artworkUrl512: String,
    val collectionName: String,
    val releaseYear: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {

    override fun equals(other: Any?): Boolean {
        return other is Track && this.trackId == other.trackId
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
}