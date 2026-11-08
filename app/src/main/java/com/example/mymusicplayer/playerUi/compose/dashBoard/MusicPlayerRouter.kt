package com.example.mymusicplayer.playerUi.compose.dashBoard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mymusicplayer.playerUi.MusicPlayerViewModel
import com.example.mymusicplayer.playerUi.MusicScreenViewState
import com.example.mymusicplayer.playerUi.compose.playerScreen.PlayerScreen

@Composable
fun MusicPlayerRouter(
    viewModel: MusicPlayerViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler(enabled = uiState.musicScreenViewState == MusicScreenViewState.PLAYER_SCREEN_VIEW) {
        viewModel.navigateToDashboard()
    }

    AnimatedContent(
        targetState = uiState.musicScreenViewState,
        transitionSpec = {
            if (targetState == MusicScreenViewState.PLAYER_SCREEN_VIEW) {
                (slideInVertically(animationSpec = tween(350)) { height -> height } + fadeIn(animationSpec = tween(350)))
                    .togetherWith(fadeOut(animationSpec = tween(200)))
            } else {
                fadeIn(animationSpec = tween(200))
                    .togetherWith(slideOutVertically(animationSpec = tween(350)) { height -> height } + fadeOut(animationSpec = tween(350)))
            }
        },
        label = "ScreenTransition"
    ) { screenState ->
        when (screenState) {
            MusicScreenViewState.DASHBOARD_VIEW -> {
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
                    currentTrack = uiState.currentTrack,
                    onClickMusic = { track -> viewModel.playPause(track) },
                    onClickPlayer = {
                        viewModel.navigateToPlayerScreen()
                    }
                )
            }
            MusicScreenViewState.PLAYER_SCREEN_VIEW -> {
                PlayerScreen(
                    musicName = uiState.currentTrack?.title,
                    artiestName = uiState.currentTrack?.artistName,
                    musicCover = uiState.currentTrack?.coverPhotoUrl,
                    position = uiState.currentPosition,
                    duration = uiState.duration,
                    onSeek = { position ->
                        viewModel.seekTo(position)
                    },
                    currentPosition = uiState.currentPosition,
                    totalDuration = uiState.currentTrack?.duration,
                    isPlaying = uiState.currentTrack?.isPlaying,
                    onCloseClick = { viewModel.navigateToDashboard() },
                    onClickMusic = {
                        viewModel.playPause(uiState.currentTrack)
                    },
                    onPreviousClick = { viewModel.previous() },
                    onNextClick = { viewModel.next() }
                )
            }
            else -> {}
        }
    }
}
