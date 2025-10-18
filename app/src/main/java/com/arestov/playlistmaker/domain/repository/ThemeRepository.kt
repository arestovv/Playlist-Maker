package com.arestov.playlistmaker.domain.repository

interface ThemeRepository {
    fun isDark(): Boolean
    fun setDark(state: Boolean)
}