package com.example.mymusicplayer.playerUi

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.mymusicplayer.MusicControllerManager
import com.example.mymusicplayer.Repository
import com.example.mymusicplayer.model.AudiusTrack
import dagger.hilt.android.lifecycle.HiltViewModel
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
            updatePlaybackState()
            if (isPlaying) {
                startProgressUpdate()
            } else {
                stopProgressUpdate()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            updatePlaybackState()
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            updatePlaybackState()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            updatePlaybackState()
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            updatePlaybackState()
        }
    }

    init {
        controllerManager.connect { controller ->
            player = controller
            controller.addListener(playerListener)
            updatePlaybackState()
            if (controller.isPlaying) {
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

    fun updatePlaybackState() {
        val activePlayer = player ?: return
        val currentMediaId = activePlayer.currentMediaItem?.mediaId
        val isPlaying = activePlayer.isPlaying
        val dur = activePlayer.duration.coerceAtLeast(0)
        val pos = activePlayer.currentPosition.coerceAtLeast(0)
        val metaData = activePlayer.mediaMetadata
        val title = metaData?.title?.toString().orEmpty()
        val artist = metaData?.artist?.toString().orEmpty()

        viewModelState.update { currentState ->
            val updatedTracks = currentState.audiusTracks?.map { track ->
                val trackIsPlaying = (track.id == currentMediaId) && isPlaying
                if (track.isPlaying != trackIsPlaying) {
                    track.copy(isPlaying = trackIsPlaying)
                } else {
                    track
                }
            }
            //val currentTrack = currentState.audiusTracks?.find { it.id == currentMediaId } ?: currentState.currentTrack
            val currentTrack = updatedTracks?.find { it.id == currentMediaId }
                ?: currentState.currentTrack

            currentState.copy(
                isPlaying = isPlaying,
                currentTitle = if (title.isNotEmpty()) title else currentTrack?.title.orEmpty(),
                currentArtist = if (artist.isNotEmpty()) artist else currentTrack?.artistName.orEmpty(),
                duration = if (dur > 0) dur else currentState.duration,
                currentPosition = pos,
                audiusTracks = updatedTracks,
                currentTrack = currentTrack
            )
        }
    }

    fun updateMetaData() {
        updatePlaybackState()
    }

    fun next() {
        val tracks = viewModelState.value.audiusTracks ?: return
        if (tracks.isEmpty()) return
        val currentMediaId = player?.currentMediaItem?.mediaId
        val currentIndex = tracks.indexOfFirst { it.id == currentMediaId }
        val nextIndex = if (currentIndex != -1 && currentIndex + 1 < tracks.size) {
            currentIndex + 1
        } else {
            0
        }
        playAudiusTrack(tracks[nextIndex])
    }

    fun previous() {
        val tracks = viewModelState.value.audiusTracks ?: return
        if (tracks.isEmpty()) return
        val currentMediaId = player?.currentMediaItem?.mediaId
        val currentIndex = tracks.indexOfFirst { it.id == currentMediaId }
        val prevIndex = if (currentIndex > 0) {
            currentIndex - 1
        } else {
            tracks.size - 1
        }
        playAudiusTrack(tracks[prevIndex])
    }

    fun seekTo(position: Long) {
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
            if (response.isNotEmpty()) {
                viewModelState.update { currentState ->
                    val currentMediaId = player?.currentMediaItem?.mediaId
                    val isPlaying = player?.isPlaying == true
                    val updatedTracks = response.map { track ->
                        track.copy(isPlaying = (track.id == currentMediaId && isPlaying))
                    }
                    currentState.copy(
                        isLoading = false,
                        audiusTracks = updatedTracks,
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

    fun playPause(track: AudiusTrack? = null) {
        val activePlayer = player
        if (track == null) {
            if (activePlayer == null) return
            val currentMediaId = activePlayer.currentMediaItem?.mediaId
            if (currentMediaId == null) {
                val firstTrack = viewModelState.value.audiusTracks?.firstOrNull()
                if (firstTrack != null) {
                    playAudiusTrack(firstTrack)
                }
            } else {
                if (activePlayer.isPlaying) {
                    activePlayer.pause()
                } else {
                    activePlayer.play()
                }
            }
        } else {
            if (activePlayer == null) {
                playAudiusTrack(track)
                return
            }
            val currentMediaId = activePlayer.currentMediaItem?.mediaId
            if (currentMediaId == track.id) {
                if (activePlayer.isPlaying) {
                    activePlayer.pause()
                } else {
                    activePlayer.play()
                }
            } else {
                playAudiusTrack(track)
            }
        }
    }

    fun playAudiusTrack(track: AudiusTrack?) {
        val activePlayer = player ?: return
        if (track == null) return
        val streamUrl = "https://api.audius.co/v1/tracks/${track.id}/stream"
        val artworkUri = track.artworkUrl?.takeIf { it.isNotBlank() }?.toUri()
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

        val trackDurationMs = (track.duration?.toLong() ?: 0L) * 1000L
        viewModelState.update { currentState ->
            val updatedTracks = currentState.audiusTracks?.map {
                it.copy(isPlaying = (it.id == track.id))
            }
            currentState.copy(
                currentTrack = track,
                currentTitle = track.title.orEmpty(),
                currentArtist = track.artistName,
                duration = if (trackDurationMs > 0) trackDurationMs else currentState.duration,
                currentPosition = 0L,
                isPlaying = true,
                audiusTracks = updatedTracks
            )
        }

        activePlayer.setMediaItem(mediaItem)
        activePlayer.prepare()
        activePlayer.play()
    }

    fun navigateToDashboard(){
        viewModelState.update {
            it.copy(
                musicScreenViewState = MusicScreenViewState.DASHBOARD_VIEW
            )
        }
    }

    fun navigateToPlayerScreen(){
        viewModelState.update {
            it.copy(
                musicScreenViewState = MusicScreenViewState.PLAYER_SCREEN_VIEW
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
    val currentTrack: AudiusTrack? = null,
    val musicScreenViewState: MusicScreenViewState? = MusicScreenViewState.DASHBOARD_VIEW
) {
    fun uiState() = MyUiState(
        isLoading = isLoading,
        isPlaying = isPlaying,
        currentTitle = currentTitle,
        currentArtist = currentArtist,
        currentPosition = currentPosition,
        duration = duration,
        audiusTracks = audiusTracks,
        currentTrack = currentTrack,
        musicScreenViewState = musicScreenViewState
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
    val currentTrack: AudiusTrack?,
    val musicScreenViewState: MusicScreenViewState?
)

enum class MusicScreenViewState(val value: String){
    DASHBOARD_VIEW("dashboard_view"),
    PLAYER_SCREEN_VIEW("player_screen_view")
}
