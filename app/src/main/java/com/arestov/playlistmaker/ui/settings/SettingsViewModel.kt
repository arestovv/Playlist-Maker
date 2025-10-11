package com.arestov.playlistmaker.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.domain.repository.ExternalNavigatorRepository
import com.arestov.playlistmaker.domain.repository.ThemeRepository
import com.arestov.playlistmaker.domain.settings.model.EmailData
import com.arestov.playlistmaker.utils.ResourceManager

class SettingsViewModel(
    private val externalNavigatorRepository: ExternalNavigatorRepository,
    private val themeRepository: ThemeRepository,
    private val resourceManager: ResourceManager,
) : ViewModel() {

    val themeStateLiveData = MutableLiveData<Boolean>().apply {
        value = themeRepository.isDarkThemeEnabled()
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        themeRepository.setDarkThemeEnabled(enabled)
        themeStateLiveData.value = enabled
    }

    fun shareApp() {
        externalNavigatorRepository.shareApp(shareAppLink = getShareAppLink())
    }

    fun openTerms() {
        externalNavigatorRepository.openTerms(termsLink = getTermsLink())
    }

    fun openSupport() {
        externalNavigatorRepository.openSupport(supportEmailData = getSupportEmailData())
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