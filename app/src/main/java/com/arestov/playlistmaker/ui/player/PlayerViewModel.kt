package com.arestov.playlistmaker.ui.player

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val historyInteractor: GetTrackHistoryInteractor,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private var timerJob: Job? = null

    private val playerStateMutableLiveData = MutableLiveData<PlayerState>(
        PlayerState.Default(progress = DEFAULT_TIMER)
    )
    val playerStateLiveData: LiveData<PlayerState> = playerStateMutableLiveData

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        resetTimer()
        mediaPlayer.release()
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            is PlayerState.Playing -> pausePlayer()
            else -> startPlayer()
        }
    }

    fun getTrack(): Track {
        return historyInteractor.getTracks().first()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(getTrack().previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            setPlayerState(PlayerState.Prepared(progress = DEFAULT_TIMER))
        }

        mediaPlayer.setOnCompletionListener {
            setPlayerState(PlayerState.Prepared(progress = DEFAULT_TIMER))
            resetTimer()
        }
    }

    private fun startPlayer() {
        //reset timer after play to the end track
        if (getPlayerState() is PlayerState.Default) {
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.start()
        setPlayerState(PlayerState.Playing(progress = getCurrentFormattedTime()))
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        setPlayerState(PlayerState.Paused(progress = getCurrentFormattedTime()))
    }

    private fun startTimerUpdate() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                setPlayerState(PlayerState.Playing(progress = getCurrentFormattedTime()))
                delay(DELAY_TIME)
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        playerStateMutableLiveData.postValue(
            PlayerState.Default(progress = DEFAULT_TIMER)
        )
    }

    private fun getCurrentFormattedTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    fun onPause() {
        pausePlayer()
    }

    private fun getPlayerState(): PlayerState? {
        return playerStateMutableLiveData.value
    }

    private fun setPlayerState(playerState: PlayerState) {
        playerStateMutableLiveData.postValue(playerState)
    }

    companion object {
        const val DEFAULT_TIMER = "00:00"
        const val DELAY_TIME = 300L
    }
}