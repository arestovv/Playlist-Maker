package com.arestov.playlistmaker.data.network

import com.arestov.playlistmaker.data.model.NetworkResponse

interface TrackNetworkClient {

    fun getTracks(text: String): NetworkResponse
}