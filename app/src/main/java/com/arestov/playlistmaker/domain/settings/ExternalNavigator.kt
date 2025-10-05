package com.arestov.playlistmaker.domain.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import androidx.core.net.toUri
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.domain.settings.model.EmailData
import retrofit2.http.Url

class ExternalNavigator(private val context: Context) {

    fun shareApp(message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_info_message)))
    }

    fun openSupport(emailData: EmailData) {
        val mailto = emailData.getMailto()
        val intent = Intent(Intent.ACTION_SENDTO, mailto.toUri())
        context.startActivity(intent)
    }

    fun openTerms(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }
}