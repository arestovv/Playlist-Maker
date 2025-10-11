package com.arestov.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import com.arestov.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val mediaPlayer: MediaPlayer,
    private val historyInteractor: GetTrackHistoryInteractor
) : ViewModel() {

    private val playerStateMutableLiveData = MutableLiveData<PlayerState>(
        PlayerState.Default(progress = DEFAULT_TIMER)
    )
    val playerStateLiveData: LiveData<PlayerState> = playerStateMutableLiveData
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        if (playerStateMutableLiveData.value is PlayerState.Playing) {
            startTimerUpdate()
        }
    }

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
            playerStateMutableLiveData.postValue(
                PlayerState.Prepared(progress = DEFAULT_TIMER)
            )
        }

        mediaPlayer.setOnCompletionListener {
            playerStateMutableLiveData.postValue(
                PlayerState.Prepared(progress = DEFAULT_TIMER)
            )
            resetTimer()
        }
    }

    private fun startPlayer() {
        //reset timer after play to the end track
        if (playerStateMutableLiveData.value is PlayerState.Default) {
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.start()
        playerStateMutableLiveData.postValue(
            PlayerState.Playing(progress = getCurrentFormattedTime())
        )
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStateMutableLiveData.postValue(
            PlayerState.Paused(progress = getCurrentFormattedTime())
        )
    }

    private fun startTimerUpdate() {
        playerStateMutableLiveData.postValue(
            PlayerState.Playing(progress = getCurrentFormattedTime())
        )
        handler.postDelayed(timerRunnable, 200)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
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

    fun onDestroy() {
        onCleared()
    }


    companion object {
        const val DEFAULT_TIMER = "00:00"
    }
}