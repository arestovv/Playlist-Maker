package com.arestov.playlistmaker.mvvm.search.data.network

import com.arestov.playlistmaker.mvvm.search.data.model.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TracksResponse>
}