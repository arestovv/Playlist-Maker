package com.arestov.playlistmaker.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arestov.playlistmaker.domain.interactor.FavoriteInteractor
import com.arestov.playlistmaker.domain.interactor.PlaylistInteractor
import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val historyInteractor: GetTrackHistoryInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var playerControl: PlayerControl? = null
    private var stateJob: Job? = null
    private var isUiInBackground = false

    private val playerStateProgressMutableLiveData = MutableLiveData<PlayerStateProgress>(
        PlayerStateProgress.Default(progress = DEFAULT_TIMER)
    )
    val playerStateProgressLiveData: LiveData<PlayerStateProgress> =
        playerStateProgressMutableLiveData

    private val stateScreenMutableLiveData = MutableLiveData<PlayerScreenState>()
    val stateScreenLiveData: LiveData<PlayerScreenState> = stateScreenMutableLiveData

    private val _trackAddedEvent = MutableSharedFlow<Pair<Playlist, Boolean>>()
    val trackAddedEvent: SharedFlow<Pair<Playlist, Boolean>> = _trackAddedEvent.asSharedFlow()

    init {
        observePlaylists()
    }

    fun setPlayerControl(control: PlayerControl) {
        playerControl = control
        stateJob?.cancel()
        stateJob = viewModelScope.launch {
            control.getPlayerState().collect { state ->
                playerStateProgressMutableLiveData.value = state
                updateNotificationForState(state)
            }
        }
    }

    fun onPlayButtonClicked() {
        when (playerStateProgressLiveData.value) {
            is PlayerStateProgress.Playing -> playerControl?.pausePlayer()
            else -> playerControl?.startPlayer()
        }
    }

    fun onUiBackground() {
        isUiInBackground = true
        if (playerStateProgressLiveData.value is PlayerStateProgress.Playing) {
            playerControl?.showNotification()
        }
    }

    fun onUiForeground() {
        isUiInBackground = false
        playerControl?.hideNotification()
    }

    private fun updateNotificationForState(state: PlayerStateProgress) {
        if (!isUiInBackground) return
        if (state is PlayerStateProgress.Playing) {
            playerControl?.showNotification()
        } else {
            playerControl?.hideNotification()
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (track.isFavorite) {
                favoriteInteractor.deleteFavoriteTrack(track)
                track.isFavorite = false
            } else {
                favoriteInteractor.addFavoriteTrack(track)
                track.isFavorite = true
            }
            stateScreenMutableLiveData.postValue(PlayerScreenState.Favorite(track.isFavorite))
        }
    }

    fun getTrack(): Track {
        return historyInteractor.getTracks().first()
    }

    private fun observePlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        stateScreenMutableLiveData.value = PlayerScreenState.Empty
                    } else {
                        stateScreenMutableLiveData.value = PlayerScreenState.Content(playlists)
                    }
                }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            val isAdded = !playlistInteractor.hasPlaylistTrack(playlist.id, track.trackId)
            if (isAdded) {
                playlistInteractor.addPlaylistTrack(playlist.id, track)
            }
            _trackAddedEvent.emit(Pair(playlist, isAdded))
        }
    }

    companion object {
        const val DEFAULT_TIMER = "00:00"
    }
}
