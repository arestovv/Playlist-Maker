package com.arestov.playlistmaker.domain.settings.impl

import android.content.Context
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.domain.settings.ExternalNavigator
import com.arestov.playlistmaker.domain.settings.SharingInteractor
import com.arestov.playlistmaker.domain.settings.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareApp(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openTerms(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openSupport(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.share_message)
    }

    private fun getSupportEmailData(): EmailData {
        val email = context.getString(R.string.email)
        val subject = context.getString(R.string.email_subject)
        val body = context.getString(R.string.email_body)
        return EmailData(email, subject, body)
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.agreement_url)
    }
}
