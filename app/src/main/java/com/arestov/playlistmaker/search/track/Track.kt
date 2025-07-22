package com.arestov.playlistmaker.search.track

data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
) {

    //Get image better quality
    fun getArtworkUrl512(): String {
        return artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    }

    override fun equals(other: Any?): Boolean {
        return other is Track && this.trackId == other.trackId
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
}

