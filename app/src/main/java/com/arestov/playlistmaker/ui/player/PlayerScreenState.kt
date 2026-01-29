package com.arestov.playlistmaker.ui.player

import com.arestov.playlistmaker.domain.search.model.Playlist

sealed interface PlayerScreenState {
    data class Content(val playlists: List<Playlist>) : PlayerScreenState
    data class Favorite(val isFavorite: Boolean) : PlayerScreenState
    data class TrackAddedToPlaylist(val playlist: Playlist, val isAdded: Boolean) :
        PlayerScreenState

    object Empty : PlayerScreenState
}