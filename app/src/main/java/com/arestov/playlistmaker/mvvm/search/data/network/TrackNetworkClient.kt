package com.arestov.playlistmaker.mvvm.search.data.network

import com.arestov.playlistmaker.mvvm.search.data.model.NetworkResponse

interface TrackNetworkClient {

    fun getTracks(text: String): NetworkResponse
}