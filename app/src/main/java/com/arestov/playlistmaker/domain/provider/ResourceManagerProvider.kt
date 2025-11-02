package com.arestov.playlistmaker.domain.provider

interface ResourceManagerRepository {
    fun getString(resId: Int): String
    fun getBoolean(resId: Int): Boolean
    fun getInt(resId: Int): Int
}
