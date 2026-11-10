package com.example.mymusicplayer.model

import com.google.gson.annotations.SerializedName

data class AudiusAlbumResponse(
    val data: List<AudiusAlbum> = emptyList()
)

data class AudiusAlbum(
    val id: String,
    @SerializedName("playlist_name")
    val playlistName: String?,
    val description: String?,
    @SerializedName("is_album")
    val isAlbum: Boolean,
    val artwork: AudiusArtwork?,
    val isPlaying: Boolean = false,
    @SerializedName("profile_picture")
    val profilePicture: AudiusCoverPhoto? = null,
    @SerializedName("track_count")
    val trackCount: Int?,
    @SerializedName("total_play_count")
    val totalPlayCount: Int?,
    val user: AudiusUser?
) {
    val artistName: String
        get() = user?.name.orEmpty()

    val artworkUrl: String?
        get() = artwork?.get("480x480")
            ?: artwork?.get("150x150")
            ?: artwork?.get("1000x1000")
            ?: artwork?.url

    val coverPhotoUrl: String?
        get() = profilePicture?.get("2000x")
            ?: profilePicture?.get("640x")
            ?: profilePicture?.url
            ?: user?.coverPhotoUrl
}