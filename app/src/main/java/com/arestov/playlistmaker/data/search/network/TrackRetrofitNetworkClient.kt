package com.arestov.playlistmaker.data.search.network

import com.arestov.playlistmaker.data.search.model.NetworkResponse
import kotlin.coroutines.cancellation.CancellationException

class TrackRetrofitNetworkClient(
    private val api: RetrofitApi
) : TrackNetworkClient {

    override suspend fun getTracks(text: String): NetworkResponse {
        return try {
            val response = api.search(text)
            response.apply { resultCode = 200 }
        } catch (_: CancellationException) {
            NetworkResponse().apply { resultCode = -1 }
        }
    }
}