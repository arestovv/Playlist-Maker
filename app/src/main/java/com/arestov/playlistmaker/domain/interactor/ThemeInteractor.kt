package com.arestov.playlistmaker.domain.interactor

interface ThemeInteractor {
    fun isDark(): Boolean
    fun setDark(state: Boolean)
}