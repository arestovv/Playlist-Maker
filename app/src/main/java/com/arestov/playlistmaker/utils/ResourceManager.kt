package com.arestov.playlistmaker.utils

import android.content.Context
import com.arestov.playlistmaker.domain.provider.ResourceManagerRepository

class ResourceManager(private val context: Context): ResourceManagerRepository {

    override fun getString(resId: Int): String {
        return context.resources.getString(resId)
    }

    override fun getBoolean(resId: Int): Boolean {
        return context.resources.getBoolean(resId)
    }

    override fun getInt(resId: Int): Int {
        return context.resources.getInteger(resId)
    }
}