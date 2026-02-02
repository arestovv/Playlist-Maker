package com.arestov.playlistmaker.ui.media

sealed class CreatePlaylistScreenState {
    data class Playlist(val playlist: com.arestov.playlistmaker.domain.search.model.Playlist) : CreatePlaylistScreenState()
    data class ImagePath(val path: String) : CreatePlaylistScreenState()
}