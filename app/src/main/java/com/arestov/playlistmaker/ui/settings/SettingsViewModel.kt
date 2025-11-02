package com.arestov.playlistmaker.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.domain.interactor.ExternalNavigationInteractor
import com.arestov.playlistmaker.domain.interactor.ThemeInteractor
import com.arestov.playlistmaker.domain.model.EmailData
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
}