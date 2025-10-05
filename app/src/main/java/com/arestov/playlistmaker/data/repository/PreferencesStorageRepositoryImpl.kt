package com.arestov.playlistmaker.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository

class PreferencesStorageRepositoryImpl(
    private val key: String,
    private val sharedPreferences: SharedPreferences
): PreferencesStorageRepository {

    override fun putString(value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    override fun getString(defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    override fun putInt(value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    override fun getInt(defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    override fun putBoolean(value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    override fun getBoolean(defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    override fun remove() {
        sharedPreferences.edit { remove(key) }
    }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }

    override fun contains(): Boolean {
        return sharedPreferences.contains(key)
    }
}