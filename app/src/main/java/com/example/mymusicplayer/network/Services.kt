package com.example.mymusicplayer.network

import com.example.mymusicplayer.model.AudiusAlbumResponse
import com.example.mymusicplayer.model.AudiusResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @GET("tracks/search")
    suspend fun searchTracks(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort_method") sortMethod: String = "relevant"
    ): AudiusResponse

    @GET("playlists/trending")
    suspend fun getTopAlbums(
        @Query("type") type: String = "album",
        @Query("time") time: String = "year",
        @Query("limit") limit: Int = 10,
        @Query("omit_tracks") omitTracks: Boolean = true
    ): AudiusAlbumResponse

    @GET("playlists/{playlist_id}/tracks")
    suspend fun getAlbumTracks(
        @Path("playlist_id") albumId: String
    ): AudiusResponse

}