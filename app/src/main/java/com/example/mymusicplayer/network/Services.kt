package com.example.mymusicplayer.network

import com.example.mymusicplayer.model.AudiusResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("tracks/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): AudiusResponse

}