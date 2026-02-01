package com.arestov.playlistmaker.data.db.entity

import androidx.room.TypeConverter

class Converters {

    // Список Long -> String
    @TypeConverter
    fun fromLongList(list: List<Long>): String {
        return list.joinToString(separator = ",")
    }

    // String -> Список Long
    @TypeConverter
    fun toLongList(data: String): List<Long> {
        if (data.isEmpty()) return emptyList()
        return data.split(",").map { it.toLong() }
    }
}