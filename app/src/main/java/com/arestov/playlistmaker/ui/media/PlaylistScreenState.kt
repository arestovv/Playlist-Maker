package com.arestov.playlistmaker.ui.media

import com.arestov.playlistmaker.domain.search.model.Playlist

sealed class PlaylistScreenState {
    data class Content(val playlists: List<Playlist>) : PlaylistScreenState()
    data object Empty : PlaylistScreenState()
}