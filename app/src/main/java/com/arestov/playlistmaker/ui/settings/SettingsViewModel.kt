package com.arestov.playlistmaker.ui.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.creator.Creator
import com.arestov.playlistmaker.domain.interactor.ExternalNavigationInteractor
import com.arestov.playlistmaker.domain.interactor.ThemeInteractor
import com.arestov.playlistmaker.domain.model.EmailData
import com.arestov.playlistmaker.ui.main.SWITCHER_DARK_THEME_STATE_KEY
import com.arestov.playlistmaker.ui.main.sharedPrefs
import com.arestov.playlistmaker.utils.ResourceManager

class SettingsViewModel(
    private val navigationInteractor: ExternalNavigationInteractor,
    private val themeInteractor: ThemeInteractor,
    private val resourceManager: ResourceManager,
) : ViewModel() {

    val themeStateLiveData = MutableLiveData<Boolean>().apply {
        value = themeInteractor.isDark()
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        themeInteractor.setDark(enabled)
        themeStateLiveData.value = enabled
    }

    fun shareApp() {
        navigationInteractor.shareApp(shareAppLink = getShareAppLink())
    }

    fun openTerms() {
        navigationInteractor.openTerms(termsLink = getTermsLink())
    }

    fun openSupport() {
        navigationInteractor.openSupport(supportEmailData = getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return resourceManager.getString(R.string.share_message)
    }

    private fun getSupportEmailData(): EmailData {
        val email = resourceManager.getString(R.string.email)
        val subject = resourceManager.getString(R.string.email_subject)
        val body = resourceManager.getString(R.string.email_body)
        return EmailData(email, subject, body)
    }

    private fun getTermsLink(): String {
        return resourceManager.getString(R.string.agreement_url)
    }

    companion object {
        fun factory(context: Context) = viewModelFactory {
            initializer {
                SettingsViewModel(
                    navigationInteractor = Creator.provideExternalNavigationInteractor(context),
                    resourceManager = Creator.provideResourceManager(context),
                    themeInteractor = Creator.provideThemeInteractor(
                        context = context.applicationContext,
                        key = SWITCHER_DARK_THEME_STATE_KEY,
                        sharedPref = sharedPrefs
                    )
                )
            }
        }
    }
}