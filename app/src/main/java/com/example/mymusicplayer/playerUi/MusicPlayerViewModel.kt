package com.example.mymusicplayer.playerUi

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.mymusicplayer.MusicManager.MusicControllerManager
import com.example.mymusicplayer.Repository
import com.example.mymusicplayer.model.AudiusAlbum
import com.example.mymusicplayer.model.AudiusTrack
import com.example.mymusicplayer.model.LyricLine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
): AndroidViewModel(application) {

    private val controllerManager = MusicControllerManager(application)
    private var player: MediaController? = null
    private var progressJob: Job? = null
    private var lyricsJob: Job? = null
    private var searchJob: Job? = null
    var searchValue by mutableStateOf("")
        private set


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
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    isLoading = true,
                )
            }
            val getAudiusAlbum = async { getAudiusAlbum() }
            val getAudiusMusicList = async { getAudiusMusic() }
            getAudiusAlbum.await()
            getAudiusMusicList.await()
            viewModelState.update {
                it.copy(
                    isLoading = false,
                )
            }
        }
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
            val trackList = when (currentState.trackState) {
                MusicListState.HOME_AUDIUS_LIST -> currentState.audiusTracks
                MusicListState.ALBUM_AUDIUS_LIST -> currentState.audiusAlbumTracks
                MusicListState.SEARCH_AUDIUS_LIST -> currentState.searchAudiusList
                else -> currentState.audiusTracks
            }
            val updatedTracks = trackList?.map { track ->
                val trackIsPlaying = track.id == currentMediaId && isPlaying

                if (track.isPlaying != trackIsPlaying) {
                    track.copy(isPlaying = trackIsPlaying)
                } else {
                    track
                }
            }
            val currentTrack = updatedTracks?.find { it.id == currentMediaId }
                ?: currentState.currentTrack
            currentState.copy(
                isPlaying = isPlaying,
                currentTitle = title.ifEmpty { currentTrack?.title.orEmpty() },
                currentArtist = artist.ifEmpty { currentTrack?.artistName.orEmpty() },
                duration = if (dur > 0) dur else currentState.duration,
                currentPosition = pos,
                audiusTracks = if (currentState.trackState == MusicListState.HOME_AUDIUS_LIST) {
                    updatedTracks
                } else {
                    currentState.audiusTracks
                },
                audiusAlbumTracks = if (currentState.trackState == MusicListState.ALBUM_AUDIUS_LIST) {
                    updatedTracks
                } else {
                    currentState.audiusAlbumTracks
                },
                searchAudiusList = if (currentState.trackState == MusicListState.SEARCH_AUDIUS_LIST) {
                    updatedTracks
                } else {
                    currentState.searchAudiusList
                },
                currentTrack = currentTrack
            )
        }
    }

    fun updateMetaData() {
        updatePlaybackState()
    }

    fun next() {
        val state = viewModelState.value
        val tracks = when (state.trackState) {
            MusicListState.HOME_AUDIUS_LIST ->
                state.audiusTracks

            MusicListState.ALBUM_AUDIUS_LIST ->
                state.audiusAlbumTracks

            MusicListState.SEARCH_AUDIUS_LIST ->
                state.searchAudiusList

            else -> state.audiusTracks
        } ?: return
        if (tracks.isEmpty()) return
        val currentMediaId = player?.currentMediaItem?.mediaId
        val currentIndex = tracks.indexOfFirst { it.id == currentMediaId }
        val nextIndex = if (currentIndex != -1) {
            (currentIndex + 1) % tracks.size
        } else {
            0
        }
        playAudiusTrack(tracks[nextIndex], state.trackState)
    }

    fun previous() {
        val state = viewModelState.value
        val tracks = when (state.trackState) {
            MusicListState.HOME_AUDIUS_LIST ->
                state.audiusTracks

            MusicListState.ALBUM_AUDIUS_LIST ->
                state.audiusAlbumTracks

            MusicListState.SEARCH_AUDIUS_LIST ->
                state.searchAudiusList

            else -> state.audiusTracks
        } ?: return
        if (tracks.isEmpty()) return
        val currentMediaId = player?.currentMediaItem?.mediaId
        val currentIndex = tracks.indexOfFirst { it.id == currentMediaId }
        val prevIndex = if (currentIndex > 0) {
            currentIndex - 1
        } else {
            tracks.size - 1
        }
        playAudiusTrack(tracks[prevIndex], state.trackState)
    }

    fun seekTo(position: Long) {
        player?.seekTo(position)
        viewModelState.update { it.copy(currentPosition = position) }
    }

    override fun onCleared() {
        stopProgressUpdate()
        controllerManager.release()
    }

    fun getAudiusAlbum(){
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    isLoading = true,
                )
            }
            val response = withContext(Dispatchers.IO){
                repository.getTopAlbums()
            }
            if (response.isNotEmpty()){
                viewModelState.update {
                    it.copy(
                        audiusAlbum = response,
                    )
                }
            } else {
                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        audiusAlbum = null,
                    )
                }
            }
        }
    }

    fun getAudiusAlbumMusic(albumId: String) {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    isLoading = true,
                    audiusAlbumTracks = null
                )
            }
            val response = withContext(Dispatchers.IO){
                repository.getAlbumTracks(albumId)
            }
            if (response.isNotEmpty()) {
                viewModelState.update { currentState ->
                    val currentMediaId = player?.currentMediaItem?.mediaId
                    val isPlaying = player?.isPlaying == true
                    val updatedTracks = response.map { track ->
                        track.copy(isPlaying = (track.id == currentMediaId && isPlaying))
                    }
                    currentState.copy(
                        isLoading = false,
                        audiusAlbumTracks = updatedTracks,
                    )
                }
            } else {
                viewModelState.update {
                    it.copy(
                        isLoading = false,
                        audiusAlbumTracks = null,
                    )
                }
            }
        }
    }


    fun getAudiusMusic(query: String = "travis scott") {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    isLoading = true,
                )
            }
            val response = withContext(Dispatchers.IO){
                repository.searchTracks(query)
            }
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

    fun playPause(track: AudiusTrack? = null, isAlbumTrack: MusicListState?) {
        val activePlayer = player
        if (track == null) {
            if (activePlayer == null) return
            val currentMediaId = activePlayer.currentMediaItem?.mediaId
            if (currentMediaId == null) {
                val firstTrack = viewModelState.value.audiusTracks?.firstOrNull()
                if (firstTrack != null) {
                    playAudiusTrack(firstTrack, isAlbumTrack)
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
                playAudiusTrack(track, isAlbumTrack)
                return
            }
            val currentMediaId = activePlayer.currentMediaItem?.mediaId
            println("playAudiusTrack 2 ${currentMediaId == track.id}")
            if (currentMediaId == track.id) {
                if (activePlayer.isPlaying) {
                    activePlayer.pause()
                } else {
                    activePlayer.play()
                }
            } else {
                playAudiusTrack(track, isAlbumTrack)
            }
        }
    }

    fun playAudiusTrack(track: AudiusTrack?, isAlbumTrack: MusicListState?) {
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
            val updatedHomeTracks = currentState.audiusTracks?.map { item ->
                item.copy(
                    isPlaying = isAlbumTrack == MusicListState.HOME_AUDIUS_LIST &&
                            item.id == track.id
                )
            }

            val updatedAlbumTracks = currentState.audiusAlbumTracks?.map { item ->
                item.copy(
                    isPlaying = isAlbumTrack == MusicListState.ALBUM_AUDIUS_LIST &&
                            item.id == track.id
                )
            }

            val updatedSearchTracks = currentState.searchAudiusList?.map { item ->
                item.copy(
                    isPlaying = isAlbumTrack == MusicListState.SEARCH_AUDIUS_LIST &&
                            item.id == track.id
                )
            }

            currentState.copy(
                trackState = isAlbumTrack,
                currentTrack = track,
                currentTitle = track.title.orEmpty(),
                currentArtist = track.artistName,
                duration = if (trackDurationMs > 0) {
                    trackDurationMs
                } else {
                    currentState.duration
                },
                currentPosition = 0L,
                isPlaying = true,

                audiusTracks = updatedHomeTracks,
                audiusAlbumTracks = updatedAlbumTracks,
                searchAudiusList = updatedSearchTracks
            )
        }

        activePlayer.setMediaItem(mediaItem)
        activePlayer.prepare()
        activePlayer.play()

        getLyrics(track)
    }

    fun getLyrics(track: AudiusTrack) {
        lyricsJob?.cancel()
        lyricsJob = viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    lyrics = emptyList(),
                    isLyricsLoading = true,
                    lyricsError = null
                )
            }
            val response = repository.getLyrics(
                trackName = cleanTrackName(track.title),
                artistName = cleanArtistName(if (track.artistName == "Travit Scott") "Travis Scott" else track.artistName),
                duration = track.duration
            )

            if (response.isNotEmpty()){
                viewModelState.update {
                    it.copy(
                        lyrics = response,
                        isLyricsLoading = false,
                        lyricsError = if (response.isEmpty()) {
                            "No synced lyrics available"
                        } else null
                    )
                }
            } else {
                viewModelState.update {
                    it.copy(
                        lyrics = emptyList(),
                        isLyricsLoading = false,
                        lyricsError = "Unable to load lyrics"
                    )
                }
            }
        }
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

    fun cleanTrackName(title: String?): String {
        return title.orEmpty()
            .replace(
                Regex("""\s*-\s*"""),
                " "
            )
            .replace(
                Regex(
                    """\s*\(?\s*(ft\.?|feat\.?|featuring)\s+.*$""",
                    RegexOption.IGNORE_CASE
                ),
                ""
            )
            .trim()
    }

    fun cleanArtistName(artist: String?): String {
        return artist.orEmpty()
            .replace(
                Regex("""\s+"""),
                " "
            )
            .trim()
    }

    fun searchMusic(query: String) {
        searchValue = query
        searchJob?.cancel()
        if (query.isBlank()) {
            viewModelState.update {
                it.copy(
                    isSearchLoader = false,
                    searchAudiusList = emptyList()
                )
            }
            return
        }
        searchJob = viewModelScope.launch {
            viewModelState.update {
                it.copy(isSearchLoader = true)
            }
            delay(400.milliseconds)
            try {
                val response = repository.searchTracks(query)
                viewModelState.update { currentState ->
                    val currentMediaId = player?.currentMediaItem?.mediaId
                    val isPlaying = player?.isPlaying == true

                    currentState.copy(
                        isSearchLoader = false,
                        searchAudiusList = response.map { track ->
                            track.copy(
                                isPlaying = track.id == currentMediaId && isPlaying
                            )
                        }
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                viewModelState.update {
                    it.copy(
                        isSearchLoader = false,
                        audiusTracks = emptyList()
                    )
                }
            }
        }
    }
}

data class MyViewModelState(
    val isLoading: Boolean? = false,
    val isSearchLoader: Boolean? = false,
    val isPlaying: Boolean? = false,
    val currentTitle: String? = "",
    val currentArtist: String? = "",
    val currentPosition: Long? = 0,
    val duration: Long? = 0,
    val trackState: MusicListState? = MusicListState.HOME_AUDIUS_LIST,
    val audiusTracks: List<AudiusTrack>? = emptyList(),
    val audiusAlbumTracks: List<AudiusTrack>? = emptyList(),
    val searchAudiusList: List<AudiusTrack>? = emptyList(),
    val audiusAlbum: List<AudiusAlbum>? = emptyList(),
    val currentTrack: AudiusTrack? = null,
    val musicScreenViewState: MusicScreenViewState? = MusicScreenViewState.DASHBOARD_VIEW,
    val lyrics: List<LyricLine> = emptyList(),
    val isLyricsLoading: Boolean = false,
    val lyricsError: String? = null,
) {
    fun uiState() = MyUiState(
        isLoading = isLoading,
        isSearchLoader = isSearchLoader,
        isPlaying = isPlaying,
        currentTitle = currentTitle,
        currentArtist = currentArtist,
        currentPosition = currentPosition,
        duration = duration,
        trackState = trackState,
        audiusTracks = audiusTracks,
        audiusAlbumTracks = audiusAlbumTracks,
        searchAudiusList = searchAudiusList,
        audiusAlbum = audiusAlbum,
        currentTrack = currentTrack,
        musicScreenViewState = musicScreenViewState,
        lyrics = lyrics,
        isLyricsLoading = isLyricsLoading,
        lyricsError = lyricsError
    )
}

data class MyUiState(
    val isLoading: Boolean?,
    val isSearchLoader: Boolean?,
    val isPlaying: Boolean?,
    val currentTitle: String?,
    val currentArtist: String?,
    val currentPosition: Long?,
    val duration: Long?,
    val trackState: MusicListState?,
    val audiusTracks: List<AudiusTrack>?,
    val audiusAlbumTracks: List<AudiusTrack>?,
    val searchAudiusList: List<AudiusTrack>?,
    val audiusAlbum: List<AudiusAlbum>?,
    val currentTrack: AudiusTrack?,
    val musicScreenViewState: MusicScreenViewState?,
    val lyrics: List<LyricLine>,
    val isLyricsLoading: Boolean,
    val lyricsError: String?
)

enum class MusicScreenViewState(val value: String){
    DASHBOARD_VIEW("dashboard_view"),
    PLAYER_SCREEN_VIEW("player_screen_view")
}

enum class UiScreenState(val value: String){
    DEFAULT_VIEW("default_view"),
    ALBUM_VIEW("album_view")
}

enum class MusicListState(val value: String){
    HOME_AUDIUS_LIST("home_audius_list"),
    ALBUM_AUDIUS_LIST("album_audius_list"),
    SEARCH_AUDIUS_LIST("search_audius_list"),
}