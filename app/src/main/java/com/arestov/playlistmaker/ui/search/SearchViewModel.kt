package com.arestov.playlistmaker.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arestov.playlistmaker.domain.search.consumer.Consumer
import com.arestov.playlistmaker.domain.search.consumer.ConsumerData
import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.domain.search.usecases.GetTrackListUseCase

class SearchViewModel(
    private val getTrackListUseCase: GetTrackListUseCase,
    private val getTrackHistoryInteractor: GetTrackHistoryInteractor
) : ViewModel() {

    private val screenState = MutableLiveData<SearchScreenState>()
    val screenStateLiveData: LiveData<SearchScreenState> = screenState

    private val historyTracks = MutableLiveData<List<Track>>()
    val historyTracksLiveData: LiveData<List<Track>> = historyTracks

    init {
        loadHistory()
    }

    fun loadHistory() {
        historyTracks.value = getTrackHistoryInteractor.getTracks()
    }

    fun addHistoryTrack(track: Track) {
        getTrackHistoryInteractor.addTrack(track)
        loadHistory()
    }

    fun clearHistoryTrack() {
        getTrackHistoryInteractor.clear()
        loadHistory()
    }

    fun hasHistoryTracks(): Boolean {
        return historyTracks.value.orEmpty().isNotEmpty()
    }

    fun searchTracks(searchText: String) {
        if (searchText.isEmpty()) return

        screenState.postValue(SearchScreenState.Loading)

        getTrackListUseCase.execute(
            text = searchText,
            consumer = object : Consumer<List<Track>> {
                override fun consume(data: ConsumerData<List<Track>>) {
                    screenState.postValue(
                        when (data) {
                            is ConsumerData.Error -> SearchScreenState.NetworkError
                            is ConsumerData.Data -> if (data.data.isEmpty()) {
                                SearchScreenState.EmptyResult
                            } else {
                                SearchScreenState.Content(data.data)
                            }
                        }
                    )
                }
            }
        )
    }
}