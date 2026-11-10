package com.example.mymusicplayer.network

import com.example.mymusicplayer.model.LyricsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LyricsApiService {

    @GET("api/search")
    suspend fun getLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String,
        //@Query("duration") duration: Int? = null
    ): List<LyricsResponse>
}