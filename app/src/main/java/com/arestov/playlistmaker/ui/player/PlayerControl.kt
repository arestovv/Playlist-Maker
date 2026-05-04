package com.arestov.playlistmaker.ui.player

import kotlinx.coroutines.flow.StateFlow

interface PlayerControl {
    fun startPlayer()
    fun pausePlayer()
    fun getPlayerState(): StateFlow<PlayerStateProgress>
    fun showNotification()
    fun hideNotification()
}
