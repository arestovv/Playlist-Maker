package com.arestov.playlistmaker.data.provider

import android.content.Context
import android.content.res.Configuration
import com.arestov.playlistmaker.domain.provider.SystemThemeProvider

class SystemThemeProviderImpl(private val context: Context) : SystemThemeProvider {

    override fun isSystemDarkThemeEnabled(): Boolean {
        return when (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }
}