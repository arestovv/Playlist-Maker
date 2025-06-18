package com.arestov.playlistmaker.search.itunes

import com.arestov.playlistmaker.search.track.Track
import com.arestov.playlistmaker.search.track.TrackResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class ITunesClientApi {
    private val baseUrl = "https://itunes.apple.com"
    private val api = createApi()

    //Build retrofit
    private fun retrofit(): Retrofit {
       return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Create api
    private fun createApi(): ITunesApi {
       return retrofit().create(ITunesApi::class.java)
    }

    //Search tracks
    fun searchTracks(
        text: String,
        onSuccess: (List<Track>) -> Unit,
        onEmpty: () -> Unit,
        onError: () -> Unit
    ) {
        api.search(text).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                val results = response.body()?.results.orEmpty()
                when {
                    response.isSuccessful && results.isNotEmpty() -> onSuccess(results)
                    response.isSuccessful -> onEmpty()
                    else -> onError()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                onError()
            }
        })
    }
}