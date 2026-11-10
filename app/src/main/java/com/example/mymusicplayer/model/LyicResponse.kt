package com.example.mymusicplayer.model

data class LyricsResponse(
    val id: Int,
    val trackName: String?,
    val artistName: String?,
    val duration: Double?,
    val instrumental: Boolean,
    val plainLyrics: String?,
    val syncedLyrics: String?
)

data class LyricLine(
    val timestampMs: Long,
    val text: String
)