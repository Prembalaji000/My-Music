package com.example.mymusicplayer.playerUi

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.mymusicplayer.MusicControllerManager
import com.example.mymusicplayer.Repository
import com.example.mymusicplayer.model.AudiusTrack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
): AndroidViewModel(application) {

    private val controllerManager = MusicControllerManager(application)

    private var player: MediaController? = null

    private var progressJob: Job? = null

    private val viewModelState = MutableStateFlow(
        MyViewModelState(
            isLoading = false
        )
    )

    val uiState = viewModelState
        .map { it.uiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.uiState())

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            viewModelState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                startProgressUpdate()
            } else {
                stopProgressUpdate()
                updatePosition()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            updateMetaData()
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            updateMetaData()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            val dur = player?.duration?.coerceAtLeast(0) ?: 0L
            viewModelState.update {
                it.copy(
                    duration = dur,
                    currentPosition = player?.currentPosition?.coerceAtLeast(0) ?: 0L
                )
            }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            updatePosition()
        }
    }

    init {
        controllerManager.connect { controller ->
            player = controller
            controller.addListener(playerListener)
            updateMetaData()
            val isPlaying = controller.isPlaying
            viewModelState.update {
                it.copy(
                    isPlaying = isPlaying,
                    duration = controller.duration.coerceAtLeast(0),
                    currentPosition = controller.currentPosition.coerceAtLeast(0)
                )
            }
            if (isPlaying) {
                startProgressUpdate()
            }
        }
        getAudiusMusic()
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                updatePosition()
                delay(500)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun updatePosition() {
        val currentPos = player?.currentPosition?.coerceAtLeast(0) ?: 0L
        val dur = player?.duration?.coerceAtLeast(0) ?: 0L
        viewModelState.update {
            it.copy(
                currentPosition = currentPos,
                duration = if (dur > 0) dur else it.duration
            )
        }
    }

    fun updateMetaData() {
        val metaData = player?.mediaMetadata
        val title = metaData?.title?.toString()
        val artist = metaData?.artist?.toString()
        viewModelState.update {
            it.copy(
                currentTitle = title ?: "",
                currentArtist = artist ?: "",
                duration = player?.duration?.coerceAtLeast(0) ?: 0L,
                currentPosition = player?.currentPosition?.coerceAtLeast(0) ?: 0L
            )
        }
    }

    fun playPause(){
        player?.let {
            if (it.isPlaying){
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun next(){
        player?.seekToNextMediaItem()
        if (viewModelState.value.isPlaying == false){
            playPause()
        }
    }

    fun previous(){
        player?.seekToPreviousMediaItem()
        if (viewModelState.value.isPlaying == false){
            playPause()
        }
    }

    fun seekTo(position: Long){
        player?.seekTo(position)
        viewModelState.update { it.copy(currentPosition = position) }
    }

    override fun onCleared() {
        stopProgressUpdate()
        controllerManager.release()
    }

    fun getAudiusMusic(query: String = "travis scott") {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    isLoading = true,
                )
            }
            val response = repository.searchTracks(query)
            if (response.isNotEmpty()){
                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        audiusTracks = response,
                    )
                }
            } else {
                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        audiusTracks = null,
                    )
                }
            }
        }
    }

    fun playAudiusTrack(track: AudiusTrack) {
        val activePlayer = player ?: return
        val streamUrl = "https://api.audius.co/v1/tracks/${track.id}/stream"
        val artworkUri = track.artworkUrl
            ?.takeIf { it.isNotBlank() }
            ?.let { it.toUri() }
        val mediaItem = MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title.orEmpty())
                    .setArtist(track.artistName)
                    .setArtworkUri(artworkUri)
                    .build()
            )
            .build()

        activePlayer.setMediaItem(mediaItem)
        activePlayer.prepare()
        activePlayer.play()

        viewModelState.update {
            it.copy(
                currentTrack = track,
                isPlaying = true
            )
        }
    }

}

data class MyViewModelState(
    val isLoading: Boolean? = false,
    val isPlaying: Boolean? = false,
    val currentTitle: String? = "",
    val currentArtist: String? = "",
    val currentPosition: Long? = 0,
    val duration: Long? = 0,
    val audiusTracks: List<AudiusTrack>? = emptyList(),
    val currentTrack: AudiusTrack? = null
) {
    fun uiState() = MyUiState(
        isLoading = isLoading,
        isPlaying = isPlaying,
        currentTitle = currentTitle,
        currentArtist = currentArtist,
        currentPosition = currentPosition,
        duration = duration,
        audiusTracks = audiusTracks,
        currentTrack = currentTrack
    )
}

data class MyUiState(
    val isLoading: Boolean?,
    val isPlaying: Boolean?,
    val currentTitle: String?,
    val currentArtist: String?,
    val currentPosition: Long?,
    val duration: Long?,
    val audiusTracks: List<AudiusTrack>?,
    val currentTrack: AudiusTrack?
)