package com.example.mymusicplayer.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

data class AudiusResponse(
    val data: List<AudiusTrack> = emptyList()
)

data class AudiusTrack(
    val id: String,
    val title: String?,
    val duration: Int?,
    val artwork: AudiusArtwork?,
    val cover_photo: AudiusCoverPhoto? = null,
    val user: AudiusUser?,
    val isPlaying: Boolean = false
) {
    val artistName: String
        get() = user?.name.orEmpty()

    val artworkUrl: String?
        get() = artwork?.get("480x480")
            ?: artwork?.get("150x150")
            ?: artwork?.get("1000x1000")
            ?: artwork?.url

    val coverPhotoUrl: String?
        get() = cover_photo?.get("2000x")
            ?: cover_photo?.get("640x")
            ?: cover_photo?.url
            ?: user?.coverPhotoUrl
}

@JsonAdapter(AudiusArtworkDeserializer::class)
data class AudiusArtwork(
    val sizes: Map<String, String> = emptyMap(),
    val url: String? = null
) {
    fun get(key: String): String? = sizes[key] ?: url
}

class AudiusArtworkDeserializer : JsonDeserializer<AudiusArtwork> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): AudiusArtwork? {
        if (json == null || json.isJsonNull) {
            return null
        }
        if (json.isJsonObject) {
            val map = mutableMapOf<String, String>()
            for ((key, value) in json.asJsonObject.entrySet()) {
                if (value.isJsonPrimitive && value.asJsonPrimitive.isString) {
                    map[key] = value.asString
                }
            }
            return AudiusArtwork(sizes = map)
        }
        if (json.isJsonPrimitive && json.asJsonPrimitive.isString) {
            return AudiusArtwork(url = json.asString)
        }
        // If json is JsonArray (e.g. []), return null safely
        return null
    }
}

@JsonAdapter(AudiusCoverPhotoDeserializer::class)
data class AudiusCoverPhoto(
    val sizes: Map<String, String> = emptyMap(),
    val url: String? = null
) {
    fun get(key: String): String? = sizes[key] ?: url
}

class AudiusCoverPhotoDeserializer : JsonDeserializer<AudiusCoverPhoto> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): AudiusCoverPhoto? {
        if (json == null || json.isJsonNull) {
            return null
        }
        if (json.isJsonObject) {
            val map = mutableMapOf<String, String>()
            for ((key, value) in json.asJsonObject.entrySet()) {
                if (value.isJsonPrimitive && value.asJsonPrimitive.isString) {
                    map[key] = value.asString
                }
            }
            return AudiusCoverPhoto(sizes = map)
        }
        if (json.isJsonPrimitive && json.asJsonPrimitive.isString) {
            return AudiusCoverPhoto(url = json.asString)
        }
        // If json is JsonArray (e.g. []), return null safely
        return null
    }
}

data class AudiusUser(
    val id: String?,
    val name: String?,
    val cover_photo: AudiusCoverPhoto? = null
) {
    val coverPhotoUrl: String?
        get() = cover_photo?.get("2000x")
            ?: cover_photo?.get("640x")
            ?: cover_photo?.url
}
