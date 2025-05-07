package com.arestov.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //Back
        val back = findViewById<MaterialToolbar>(R.id.toolbar_settings_screen)
        back.setNavigationOnClickListener {
            finish()
        }

        //Share app
        val buttonShare = findViewById<MaterialTextView>(R.id.button_share)
        buttonShare.setOnClickListener {
            val message = getString(R.string.share_message)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_info_message)))
        }

        //Write to support
        val buttonSupport = findViewById<MaterialTextView>(R.id.button_support)
        buttonSupport.setOnClickListener {
            val email = getString(R.string.email)
            val subject = getString(R.string.email_subject)
            val body = getString(R.string.email_body)
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            val uriText = "mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
            emailIntent.data = Uri.parse(uriText)
            startActivity(emailIntent)
        }

        //Agreement
        val buttonAgreement = findViewById<MaterialTextView>(R.id.button_agreement)
        buttonAgreement.setOnClickListener {
            val url = "https://yandex.ru/legal/practicum_offer/"
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(agreementIntent)
        }
    }
}