package com.arestov.playlistmaker.domain.repository

import com.arestov.playlistmaker.domain.model.EmailData

interface ExternalNavigationRepository  {
    fun shareApp(shareAppLink: String)
    fun openTerms(termsLink: String)
    fun openSupport(supportEmailData: EmailData)

}