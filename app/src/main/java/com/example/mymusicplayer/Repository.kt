package com.example.mymusicplayer

import com.example.mymusicplayer.model.AudiusAlbum
import com.example.mymusicplayer.model.AudiusTrack
import com.example.mymusicplayer.network.ApiService
import javax.inject.Inject

class Repository @Inject constructor(
    private val service: ApiService,
) {

   /* suspend fun searchTracks(
        query: String
    ): Result<List<AudiusTrack>> {
        return try {
            val response = service.searchTracks(query)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
*/
    suspend fun searchTracks(
        query: String
    ): List<AudiusTrack> {
        val response = service.searchTracks(query)
        return response.data
    }

    suspend fun getTopAlbums(): List<AudiusAlbum>{
        val response = service.getTopAlbums()
        return response.data
    }

    suspend fun getAlbumTracks(albumId: String) : List<AudiusTrack> {
        val response = service.getAlbumTracks(albumId)
        return response.data
    }
}