package com.arestov.playlistmaker.domain.interactor.impl

import com.arestov.playlistmaker.domain.interactor.ExternalNavigationInteractor
import com.arestov.playlistmaker.domain.model.EmailData
import com.arestov.playlistmaker.domain.repository.ExternalNavigationRepository

class ExternalNavigationInteractorImpl(
    private val repository: ExternalNavigationRepository
) : ExternalNavigationInteractor {

    override fun shareApp(shareAppLink: String) {
        repository.shareApp(shareAppLink)
    }

   override fun openSupport(supportEmailData: EmailData) {
       repository.openSupport(supportEmailData)
    }

    override fun openTerms(termsLink: String) {
       repository.openTerms(termsLink)
    }
}