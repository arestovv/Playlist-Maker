package com.arestov.playlistmaker.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arestov.playlistmaker.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<PlaylistScreenState>()
    val screenStateLiveData: LiveData<PlaylistScreenState> = _screenState

    fun start() {
        observePlaylists()
    }

    fun observePlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylist()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        _screenState.value = PlaylistScreenState.Empty
                    } else {
                        _screenState.value = PlaylistScreenState.Content(playlists)
                    }
                }
        }
    }
}