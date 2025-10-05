package com.arestov.playlistmaker.domain.settings.model

import android.net.Uri

data class EmailData(
    val email: String,
    val subject: String,
    val body: String,
) {
    fun getMailto(): String {
        return "mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
    }
}