package com.arestov.playlistmaker.domain.interactor.impl

import com.arestov.playlistmaker.domain.interactor.ThemeInteractor
import com.arestov.playlistmaker.domain.repository.ThemeRepository

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {

    override fun isDark(): Boolean {
        return themeRepository.isDark()
    }

    override fun setDark(state: Boolean) {
        themeRepository.setDark(state)
    }
}