package com.arestov.playlistmaker.ui.media

import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.domain.search.model.Track

sealed class PlaylistInfoScreenState {
    data class Content(val playlist: Playlist, val tracks: List<Track>) : PlaylistInfoScreenState()
    data object Toast : PlaylistInfoScreenState()
    data object Empty : PlaylistInfoScreenState()
}