package com.arestov.playlistmaker.mvvm.search.data.network

import com.arestov.playlistmaker.mvvm.search.data.model.NetworkResponse

class TrackRetrofitNetworkClient : TrackNetworkClient {
    override fun getTracks(text: String): NetworkResponse {
        return try {
            val response = RetrofitClient.api.search(text).execute()
            val networkResponse = response.body() ?: NetworkResponse()

            networkResponse.apply() { resultCode = response.code() }
        } catch (_: Exception) {
            NetworkResponse().apply() { resultCode = -1 }

        }
    }
}