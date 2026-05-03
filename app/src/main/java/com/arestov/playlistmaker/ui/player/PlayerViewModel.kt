package com.arestov.playlistmaker.ui.player

import android.media.MediaPlayer
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val historyInteractor: GetTrackHistoryInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private var timerJob: Job? = null

    private val playerStateProgressMutableLiveData = MutableLiveData<PlayerStateProgress>(
        PlayerStateProgress.Default(progress = DEFAULT_TIMER)
    )
    val playerStateProgressLiveData: LiveData<PlayerStateProgress> =
        playerStateProgressMutableLiveData

    private val stateScreenMutableLiveData = MutableLiveData<PlayerScreenState>()
    val stateScreenLiveData: LiveData<PlayerScreenState> = stateScreenMutableLiveData

    init {
        preparePlayer()
        observePlaylists()
    }

    override fun onCleared() {
        super.onCleared()
        resetTimer()
        mediaPlayer.release()
    }

    fun onPlayButtonClicked() {
        when (playerStateProgressLiveData.value) {
            is PlayerStateProgress.Playing -> pausePlayer()
            else -> startPlayer()
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

    private fun preparePlayer() {
        mediaPlayer.setDataSource(getTrack().previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            setPlayerState(PlayerStateProgress.Prepared(progress = DEFAULT_TIMER))
        }

        mediaPlayer.setOnCompletionListener {
            setPlayerState(PlayerStateProgress.Prepared(progress = DEFAULT_TIMER))
            resetTimer()
        }
    }

    private fun startPlayer() {
        //reset timer after play to the end track
        if (getPlayerState() is PlayerStateProgress.Default) {
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.start()
        setPlayerState(PlayerStateProgress.Playing(progress = getCurrentFormattedTime()))
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        setPlayerState(PlayerStateProgress.Paused(progress = getCurrentFormattedTime()))
    }

    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                setPlayerState(PlayerStateProgress.Playing(progress = getCurrentFormattedTime()))
                delay(DELAY_TIME)
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        playerStateProgressMutableLiveData.postValue(
            PlayerStateProgress.Default(progress = DEFAULT_TIMER)
        )
    }

    private fun getCurrentFormattedTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    fun onPause() {
        pausePlayer()
    }

    private fun getPlayerState(): PlayerStateProgress? {
        return playerStateProgressMutableLiveData.value
    }

    private fun setPlayerState(playerStateProgress: PlayerStateProgress) {
        playerStateProgressMutableLiveData.postValue(playerStateProgress)
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
            if (!playlistInteractor.hasPlaylistTrack(playlist.id, track.trackId)) {
                playlistInteractor.addPlaylistTrack(playlist.id, track)
                stateScreenMutableLiveData.postValue(
                    PlayerScreenState.TrackAddedToPlaylist(playlist, true)
                )
            } else {
                stateScreenMutableLiveData.postValue(
                    PlayerScreenState.TrackAddedToPlaylist(playlist, false)
                )
            }

        }
    }

    companion object {
        const val DEFAULT_TIMER = "00:00"
        const val DELAY_TIME = 300L
    }
}