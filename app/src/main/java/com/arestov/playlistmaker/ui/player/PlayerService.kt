package com.arestov.playlistmaker.ui.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.arestov.playlistmaker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerService : Service(), PlayerControl {

    private val binder = PlayerBinder()
    private val mediaPlayer = MediaPlayer()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    private var trackUrl: String = ""
    private var trackName: String = ""
    private var artistName: String = ""

    private val playerStateFlow = MutableStateFlow<PlayerStateProgress>(
        PlayerStateProgress.Default(progress = DEFAULT_TIMER)
    )

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder {
        trackUrl = intent.getStringExtra(EXTRA_URL).orEmpty()
        trackName = intent.getStringExtra(EXTRA_TRACK_NAME).orEmpty()
        artistName = intent.getStringExtra(EXTRA_ARTIST_NAME).orEmpty()
        preparePlayer()
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        coroutineScope.cancel()
        mediaPlayer.release()
    }

    override fun startPlayer() {
        if (playerStateFlow.value is PlayerStateProgress.Default) {
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.start()
        playerStateFlow.value = PlayerStateProgress.Playing(progress = getCurrentFormattedTime())
        startTimerUpdate()
    }

    override fun pausePlayer() {
        timerJob?.cancel()
        mediaPlayer.pause()
        playerStateFlow.value = PlayerStateProgress.Paused(progress = getCurrentFormattedTime())
    }

    override fun getPlayerState(): StateFlow<PlayerStateProgress> = playerStateFlow.asStateFlow()

    override fun showNotification() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateFlow.value = PlayerStateProgress.Prepared(progress = DEFAULT_TIMER)
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerStateFlow.value = PlayerStateProgress.Prepared(progress = DEFAULT_TIMER)
        }
    }

    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = coroutineScope.launch {
            while (mediaPlayer.isPlaying) {
                playerStateFlow.value =
                    PlayerStateProgress.Playing(progress = getCurrentFormattedTime())
                delay(DELAY_TIME)
            }
        }
    }

    private fun getCurrentFormattedTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(getString(R.string.app_name))
        .setContentText("$artistName - $trackName")
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    companion object {
        const val EXTRA_URL = "extra_url"
        const val EXTRA_TRACK_NAME = "extra_track_name"
        const val EXTRA_ARTIST_NAME = "extra_artist_name"

        private const val CHANNEL_ID = "player_service_channel"
        private const val NOTIFICATION_ID = 1
        private const val DEFAULT_TIMER = "00:00"
        private const val DELAY_TIME = 300L
    }
}
