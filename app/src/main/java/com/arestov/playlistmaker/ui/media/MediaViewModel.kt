package com.arestov.playlistmaker.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arestov.playlistmaker.domain.repository.FavoriteRepository
import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.domain.search.repository.TrackRepository
import kotlinx.coroutines.launch

class MediaViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val trackRepository: TrackRepository,
    private val historyInteractor: GetTrackHistoryInteractor,
) : ViewModel() {

    private val _screenState = MutableLiveData<FavoriteScreenState>()
    val screenStateLiveData: LiveData<FavoriteScreenState> = _screenState

    init {
        observeFavoriteTracks()
    }

    fun observeFavoriteTracks() {
        viewModelScope.launch {
            favoriteRepository.getFavoriteTracks()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        _screenState.value = FavoriteScreenState.Empty
                    } else {
                        _screenState.value = FavoriteScreenState.Content(tracks)
                    }
                }
        }
    }

    fun setFavorite(tracks: List<Track>) {
        viewModelScope.launch {
            trackRepository.setFavorite(tracks)
        }
    }

    fun addHistoryTrack(track: Track) {
        historyInteractor.addTrack(track)
    }
}