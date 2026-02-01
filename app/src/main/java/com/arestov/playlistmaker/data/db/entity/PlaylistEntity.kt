package com.arestov.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "playlist_table")
@TypeConverters(Converters::class)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val imageUri: String,
    val trackList: List<Long> = emptyList(),
    val trackCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)