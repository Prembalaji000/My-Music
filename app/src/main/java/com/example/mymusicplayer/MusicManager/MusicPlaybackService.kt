package com.example.mymusicplayer.MusicManager

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.core.net.toUri

class MusicPlaybackService: MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(
            this, player
        ).setCallback(object : MediaSession.Callback {
            @OptIn(UnstableApi::class)
            override fun onConnect(
                session: MediaSession,
                controller: MediaSession.ControllerInfo
            ): MediaSession.ConnectionResult {

                val commands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                    .buildUpon()
                    .build()

                val playerCommands = MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS

                return MediaSession.ConnectionResult.AcceptedResultBuilder(
                    session
                )
                    .setAvailableSessionCommands(commands)
                    .setAvailablePlayerCommands(playerCommands)
                    .build()
            }
        })
            .build()

        val musicList = listOf(
            MediaItem.Builder()
                .setMediaId("1")
                .setUri("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("Sample Music 1")
                        .setArtist("Unknown Artist")
                        .setArtworkUri("https://images.unsplash.com/photo-1516280440614-37939bbacd81?w=1000".toUri())
                        .build()
                )
                .build(),

            MediaItem.Builder()
                .setMediaId("2")
                .setUri("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("Sample Music 2")
                        .setArtist("Unknown Artist")
                        .build()
                )
                .build()
        )

        player.setMediaItems(musicList)
        player.prepare()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
    }
}