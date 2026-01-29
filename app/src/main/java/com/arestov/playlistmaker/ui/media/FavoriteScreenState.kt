package com.arestov.playlistmaker.ui.media

import com.arestov.playlistmaker.domain.search.model.Track

sealed interface FavoriteScreenState {
    data class Content(val tracks: List<Track>) : FavoriteScreenState
    data object Empty : FavoriteScreenState
}