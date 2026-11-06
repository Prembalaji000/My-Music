package com.example.mymusicplayer

import com.example.mymusicplayer.model.AudiusArtwork
import com.example.mymusicplayer.model.AudiusArtworkDeserializer
import com.example.mymusicplayer.model.AudiusResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AudiusResponseTest {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(AudiusArtwork::class.java, AudiusArtworkDeserializer())
        .create()

    @Test
    fun testArtworkAsArray() {
        val json = """
            {
              "data": [
                {
                  "id": "123",
                  "title": "Test Song",
                  "duration": 200,
                  "artwork": [],
                  "user": { "id": "u1", "name": "Test Artist" }
                }
              ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, AudiusResponse::class.java)
        assertNotNull(response)
        assertEquals(1, response.data.size)
        val track = response.data[0]
        assertEquals("123", track.id)
        assertNull(track.artwork)
        assertNull(track.artworkUrl)
    }

    @Test
    fun testArtworkAsObject() {
        val json = """
            {
              "data": [
                {
                  "id": "123",
                  "title": "Test Song",
                  "duration": 200,
                  "artwork": {
                    "150x150": "https://image.com/150.jpg",
                    "480x480": "https://image.com/480.jpg"
                  },
                  "user": { "id": "u1", "name": "Test Artist" }
                }
              ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, AudiusResponse::class.java)
        assertNotNull(response)
        assertEquals(1, response.data.size)
        val track = response.data[0]
        assertEquals("https://image.com/480.jpg", track.artworkUrl)
        assertEquals("https://image.com/150.jpg", track.artwork?.get("150x150"))
    }

    @Test
    fun testArtworkAsString() {
        val json = """
            {
              "data": [
                {
                  "id": "123",
                  "title": "Test Song",
                  "duration": 200,
                  "artwork": "https://image.com/direct.jpg",
                  "user": { "id": "u1", "name": "Test Artist" }
                }
              ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, AudiusResponse::class.java)
        assertNotNull(response)
        assertEquals(1, response.data.size)
        val track = response.data[0]
        assertEquals("https://image.com/direct.jpg", track.artworkUrl)
    }

    @Test
    fun testArtworkAsNull() {
        val json = """
            {
              "data": [
                {
                  "id": "123",
                  "title": "Test Song",
                  "duration": 200,
                  "artwork": null,
                  "user": { "id": "u1", "name": "Test Artist" }
                }
              ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, AudiusResponse::class.java)
        assertNotNull(response)
        assertEquals(1, response.data.size)
        val track = response.data[0]
        assertNull(track.artwork)
        assertNull(track.artworkUrl)
    }
}
