package com.arestov.playlistmaker.domain.settings.model

data class EmailData(
    val email: String,
    val subject: String,
    val body: String,
)