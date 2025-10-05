package com.arestov.playlistmaker.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arestov.playlistmaker.creator.Creator
import com.arestov.playlistmaker.domain.settings.SharingInteractor
import com.arestov.playlistmaker.ui.main.SWITCHER_DARK_THEME_STATE_KEY
import com.arestov.playlistmaker.ui.main.sharedPrefs

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    val themeRepository = Creator.provideThemeRepository(
        key = SWITCHER_DARK_THEME_STATE_KEY,
        sharedPref = sharedPrefs
    )
    private val themeStateMutableLiveData = MutableLiveData<Boolean>().apply {
        value = themeRepository.isAppDarkThemeEnabled()
    }
    val themeStateLiveData = themeStateMutableLiveData;

    fun setDarkThemeEnabled(state: Boolean) {
        themeRepository.setDarkThemeEnabled(state)
        themeStateMutableLiveData.value = state
    }

    fun shareApp(){
        sharingInteractor.shareApp()
    }

    fun openTerms(){
        sharingInteractor.openTerms()
    }

    fun openSupport(){
        sharingInteractor.openSupport()
    }
}