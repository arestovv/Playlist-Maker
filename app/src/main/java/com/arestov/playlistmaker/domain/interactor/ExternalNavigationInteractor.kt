package com.arestov.playlistmaker.domain.interactor

import com.arestov.playlistmaker.domain.model.EmailData

interface ExternalNavigationInteractor {
    fun shareApp(shareAppLink: String)
    fun openSupport(supportEmailData: EmailData)
    fun openTerms(termsLink: String)
}