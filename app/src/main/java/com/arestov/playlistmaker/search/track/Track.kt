package com.arestov.playlistmaker.search.track

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
) {
    override fun equals(other: Any?): Boolean {
        return other is Track && this.trackId == other.trackId
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
}

