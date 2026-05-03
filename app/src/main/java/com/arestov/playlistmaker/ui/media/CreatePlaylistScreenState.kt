package com.arestov.playlistmaker.ui.media

import com.arestov.playlistmaker.domain.search.model.Playlist

sealed interface CreatePlaylistScreenState {
    data class Content(val playlist: Playlist) : CreatePlaylistScreenState
    data class ImagePath(val path: String) : CreatePlaylistScreenState
}