package com.arestov.playlistmaker.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.arestov.playlistmaker.app.App
import com.arestov.playlistmaker.PLAYLIST_MAKER_PREFERENCES
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.SWITCHER_DARK_THEME_STATE_KEY
import com.arestov.playlistmaker.utils.ScreensHolder
import com.arestov.playlistmaker.utils.ScreensHolder.Screens.SETTINGS
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

lateinit var sharedPrefs: SharedPreferences

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        //Back
        val back = findViewById<MaterialToolbar>(R.id.toolbar_settings_screen)
        back.navigationContentDescription = getString(R.string.settings)
        back.setNavigationOnClickListener {
            finish()
        }

        //Switch theme
        val switcherTheme = findViewById<SwitchMaterial>(R.id.switcher_theme)
        //Set switcher state from file preferences
        switcherTheme.isChecked = sharedPrefs.getBoolean(SWITCHER_DARK_THEME_STATE_KEY, false)
        //Switch listener
        switcherTheme.setOnCheckedChangeListener { switcher, checked ->
            //Save switcher state to file preferences
            (applicationContext as App).setTheme(checked, sharedPrefs)
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
            val text = "mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
            emailIntent.data = text.toUri()
            startActivity(emailIntent)
        }

        //Agreement
        val buttonAgreement = findViewById<MaterialTextView>(R.id.button_agreement)
        buttonAgreement.setOnClickListener {
            val url = getString(R.string.agreement_url)
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(agreementIntent)
        }
    }
}