package com.arestov.playlistmaker.data.search.network

import com.arestov.playlistmaker.data.search.model.NetworkResponse

interface TrackNetworkClient {

    fun getTracks(text: String): NetworkResponse
}