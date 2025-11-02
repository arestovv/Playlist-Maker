package com.arestov.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.domain.repository.ExternalNavigationRepository
import com.arestov.playlistmaker.domain.model.EmailData

class ExternalNavigationRepositoryImpl(private val context: Context) :
    ExternalNavigationRepository {

    override fun shareApp(shareAppLink: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareAppLink)
        }
        val chooser = Intent.createChooser(
            intent, context.getString(R.string.share_info_message)
        )
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    override fun openSupport(supportEmailData: EmailData) {
        val mailto = "mailto:${supportEmailData.email}" +
                "?subject=${Uri.encode(supportEmailData.subject)}" +
                "&body=${Uri.encode(supportEmailData.body)}"

        val intent = Intent(Intent.ACTION_SENDTO, mailto.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun openTerms(termsLink: String) {
        val intent = Intent(Intent.ACTION_VIEW, termsLink.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}