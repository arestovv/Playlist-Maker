package com.arestov.playlistmaker.ui.player

sealed class PlayerStateProgress(open val progress: String) {
    data class Default(override val progress: String) : PlayerStateProgress(progress)
    data class Prepared(override val progress: String) : PlayerStateProgress(progress)
    data class Playing(override val progress: String) : PlayerStateProgress(progress)
    data class Paused(override val progress: String) : PlayerStateProgress(progress)
}