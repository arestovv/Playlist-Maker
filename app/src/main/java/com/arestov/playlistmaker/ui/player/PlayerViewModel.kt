package com.arestov.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val url: String,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private val screenStateMutableLiveData = MutableLiveData<ScreenState>(
        ScreenState.Default(progress = DEFAULT_TIMER)
    )
    val screenStateLiveData: LiveData<ScreenState> = screenStateMutableLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if (screenStateMutableLiveData.value is ScreenState.Playing) {
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
        when (screenStateLiveData.value) {
            is ScreenState.Playing -> pausePlayer()
            else -> startPlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            screenStateMutableLiveData.postValue(
                ScreenState.Prepared(progress = DEFAULT_TIMER)
            )
        }

        mediaPlayer.setOnCompletionListener {
            screenStateMutableLiveData.postValue(
                ScreenState.Prepared(progress = DEFAULT_TIMER)
            )
            resetTimer()
        }
    }

    private fun startPlayer() {
        //reset timer after play to the end track
        if (screenStateMutableLiveData.value is ScreenState.Default) {
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.start()
        screenStateMutableLiveData.postValue(
            ScreenState.Playing(progress = getCurrentFormattedTime())
        )
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        screenStateMutableLiveData.postValue(
            ScreenState.Paused(progress = getCurrentFormattedTime())
        )
    }

    private fun startTimerUpdate() {
        screenStateMutableLiveData.postValue(
            ScreenState.Playing(progress = getCurrentFormattedTime())
        )
        handler.postDelayed(timerRunnable, 200)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        screenStateMutableLiveData.postValue(
            ScreenState.Default(progress = DEFAULT_TIMER)
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

    sealed class ScreenState(open val progress: String) {
        data class Default(override val progress: String) : ScreenState(progress)
        data class Prepared(override val progress: String) : ScreenState(progress)
        data class Playing(override val progress: String) : ScreenState(progress)
        data class Paused(override val progress: String) : ScreenState(progress)
    }

    companion object {
        const val DEFAULT_TIMER = "00:00"
    }
}