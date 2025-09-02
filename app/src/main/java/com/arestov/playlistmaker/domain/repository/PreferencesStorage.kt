package com.arestov.playlistmaker.domain.repository

interface PreferencesStorage {
    fun putString(value: String)
    fun getString(defaultValue: String? = null): String?
    fun putInt(value: Int)
    fun getInt(defaultValue: Int = 0): Int
    fun putBoolean(value: Boolean)
    fun getBoolean(defaultValue: Boolean = false): Boolean
    fun remove()
    fun clear()
    fun contains(key: String): Boolean
}