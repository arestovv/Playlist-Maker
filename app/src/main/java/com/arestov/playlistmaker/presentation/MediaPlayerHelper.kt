package com.arestov.playlistmaker.presentation

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.arestov.playlistmaker.domain.model.Track
import com.arestov.playlistmaker.utils.Converter

class MediaPlayerHelper(private val track: Track) {

    private var playbackStateListener: OnPlaybackStateChangedListener? = null
    private var timerTickListener: OnTimerTickListener? = null
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimerRunnable = Runnable { updateTimer() }

    private var mediaPlayer = MediaPlayer()
    private var state = STATE_DEFAULT
    private var delay = 400L

    fun prepare() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            state = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            state = STATE_PREPARED
            handler.removeCallbacks(updateTimerRunnable)
            // Callback track is completed
            playbackStateListener?.onPlaybackCompleted()
            // Reset timer
            timerTickListener?.onTimerTick(Converter.Companion.mmToSs(0))
            // Reset track
            mediaPlayer.seekTo(0)
        }
    }

    fun start() {
        mediaPlayer.start()
        state = STATE_PLAYING
        handler.post(updateTimerRunnable)
    }

    fun pause() {
        mediaPlayer.pause()
        state = STATE_PAUSED
    }

    fun isPlaying(): Boolean {
        return state == STATE_PLAYING
    }

    fun release() {
        mediaPlayer.release()
        handler.removeCallbacks(updateTimerRunnable);
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    private fun updateTimer() {
        if (mediaPlayer.isPlaying) {
            val positionMs = mediaPlayer.currentPosition
            val timerText = Converter.Companion.mmToSs(positionMs.toLong())
            timerTickListener?.onTimerTick(timerText)
        }
        handler.postDelayed(updateTimerRunnable, delay)
    }

    fun setOnTimerTickListener(listener: OnTimerTickListener) {
        timerTickListener = listener
    }

    fun setOnPlaybackStateChangedListener(listener: OnPlaybackStateChangedListener) {
        playbackStateListener = listener
    }

    interface OnPlaybackStateChangedListener {
        fun onPlaybackCompleted()
    }

    interface OnTimerTickListener {
        fun onTimerTick(formattedTime: String)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}