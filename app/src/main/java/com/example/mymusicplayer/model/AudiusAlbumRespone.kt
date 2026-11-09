package com.example.mymusicplayer.model

data class AudiusAlbumResponse(
    val data: List<AudiusAlbum> = emptyList()
)

data class AudiusAlbum(
    val id: String,
    val playlist_name: String?,
    val description: String?,
    val is_album: Boolean,
    val artwork: AudiusArtwork?,
    val isPlaying: Boolean = false,
    val profile_picture: AudiusCoverPhoto? = null,
    val track_count: Int?,
    val total_play_count: Int?,
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
        get() = profile_picture?.get("2000x")
            ?: profile_picture?.get("640x")
            ?: profile_picture?.url
            ?: user?.coverPhotoUrl
}