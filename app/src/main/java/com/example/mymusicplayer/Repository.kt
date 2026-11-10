package com.example.mymusicplayer

import com.example.mymusicplayer.Utils.LrcParser
import com.example.mymusicplayer.model.AudiusAlbum
import com.example.mymusicplayer.model.AudiusTrack
import com.example.mymusicplayer.model.LyricLine
import com.example.mymusicplayer.network.ApiService
import com.example.mymusicplayer.network.LyricsApiService
import javax.inject.Inject

class Repository @Inject constructor(
    private val service: ApiService,
    private val lyricService: LyricsApiService
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

    suspend fun getLyrics(
        trackName: String,
        artistName: String,
        duration: Int?
    ): List<LyricLine> {
        val responseList = lyricService.getLyrics(
            trackName = trackName,
            artistName = artistName,
            //duration = duration
        )
        val response = responseList.firstOrNull()
        val syncedLyrics = response?.syncedLyrics
        return LrcParser.parse(syncedLyrics.orEmpty())
    }
}