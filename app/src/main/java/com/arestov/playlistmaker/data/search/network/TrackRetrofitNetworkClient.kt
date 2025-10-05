package com.arestov.playlistmaker.data.search.network

import com.arestov.playlistmaker.data.search.model.NetworkResponse

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