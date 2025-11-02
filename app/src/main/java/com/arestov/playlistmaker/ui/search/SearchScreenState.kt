package com.arestov.playlistmaker.ui.search

import com.arestov.playlistmaker.domain.search.model.Track

sealed class SearchScreenState {
    data object Loading : SearchScreenState()
    data class Content(val tracks: List<Track>) : SearchScreenState()
    data object EmptyResult : SearchScreenState()
    data object NetworkError : SearchScreenState()
    data class HistoryContent(val historyTracks: List<Track>) : SearchScreenState()
}