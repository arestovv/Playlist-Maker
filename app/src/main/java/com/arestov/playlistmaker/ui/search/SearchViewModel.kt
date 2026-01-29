package com.arestov.playlistmaker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.domain.search.repository.TrackRepository
import com.arestov.playlistmaker.domain.search.usecases.GetTrackListUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getTrackListUseCase: GetTrackListUseCase,
    private val getTrackHistoryInteractor: GetTrackHistoryInteractor,
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val screenState = MutableLiveData<SearchScreenState>()
    val screenStateLiveData: LiveData<SearchScreenState> = screenState

    private var previousText = ""
    private var searchJob: Job? = null

    init {
        loadHistory()
    }

    fun loadHistory() {
        val tracks = getTrackHistoryInteractor.getTracks()
        if (tracks.isNotEmpty()) {
            screenState.postValue(SearchScreenState.HistoryContent(historyTracks = tracks))
        } else {
            screenState.postValue(SearchScreenState.EmptyHistory)
        }

    }

    fun addHistoryTrack(track: Track) {
        getTrackHistoryInteractor.addTrack(track)
    }

    fun clearHistoryTrack() {
        getTrackHistoryInteractor.clear()
        loadHistory()
    }

    fun onSearchTextChanged(searchText: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (searchText.isBlank()) {
                loadHistory()
                return@launch
            }
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracks(searchText)
        }
    }

    fun setFavorite(tracks: List<Track>) {
        viewModelScope.launch {
            trackRepository.setFavorite(tracks)
        }
    }

    fun searchTracks(searchText: String) {
        if (searchText == previousText) return

        screenState.postValue(SearchScreenState.Loading)
        previousText = searchText
        viewModelScope.launch {
            getTrackListUseCase.execute(searchText)
                .collect { (tracks, error) ->
                    val newState = when {
                        error != null -> SearchScreenState.NetworkError
                        tracks.isNullOrEmpty() -> SearchScreenState.EmptyResult
                        else -> SearchScreenState.Content(tracks)
                    }
                    screenState.postValue(newState)
                }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}