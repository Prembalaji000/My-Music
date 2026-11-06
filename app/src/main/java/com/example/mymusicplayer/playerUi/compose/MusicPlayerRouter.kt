package com.example.mymusicplayer.playerUi.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mymusicplayer.playerUi.MusicPlayerViewModel

@Composable
fun MusicPlayerRouter(
    viewModel: MusicPlayerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MusicPlayerScreen(
        title = uiState.currentTitle ?: "",
        artist = uiState.currentArtist ?: "",
        position = uiState.currentPosition ?: 0L,
        duration = uiState.duration ?: 0L,
        isPlaying = uiState.isPlaying ?: false,
        onPreviousClick = { viewModel.previous() },
        onPlayPauseClick = { viewModel.playPause() },
        onNextClick = { viewModel.next() },
        onSeek = { position -> viewModel.seekTo(position) },
        audiusMusicList = uiState.audiusTracks,
        onClickMusic = { viewModel.playAudiusTrack(it) }
    )
}