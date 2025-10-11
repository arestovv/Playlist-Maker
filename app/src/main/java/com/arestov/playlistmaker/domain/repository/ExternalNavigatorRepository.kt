package com.arestov.playlistmaker.domain.repository

import com.arestov.playlistmaker.domain.settings.model.EmailData

interface ExternalNavigatorRepository  {
    fun shareApp(shareAppLink: String)
    fun openTerms(termsLink: String)
    fun openSupport(supportEmailData: EmailData)

}