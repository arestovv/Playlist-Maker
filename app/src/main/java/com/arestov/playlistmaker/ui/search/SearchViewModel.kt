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
    private var previousText = ""

    init {
        loadHistory()
    }

    fun loadHistory() {
        if (getTrackHistoryInteractor.getTracks().isNotEmpty()) {
            screenState.postValue(
                SearchScreenState.HistoryContent(
                    historyTracks = getTrackHistoryInteractor.getTracks()
                )
            )
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

    fun searchTracks(searchText: String) {
        if (searchText.isEmpty() || searchText == previousText) return

        screenState.postValue(SearchScreenState.Loading)
        previousText = searchText

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