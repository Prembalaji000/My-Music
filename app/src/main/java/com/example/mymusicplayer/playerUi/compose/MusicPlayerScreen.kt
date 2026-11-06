package com.example.mymusicplayer.playerUi.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mymusicplayer.model.AudiusTrack

@Preview
@Composable
fun MusicPlayerScreenPreview(){
    MusicPlayerScreen(
        title = "Music",
        artist = "Justin",
        position = 0,
        duration = 0,
        isPlaying = true,
        onNextClick = {},
        onPreviousClick = {},
        onPlayPauseClick = {},
        onSeek = {},
        audiusMusicList = null,
        onClickMusic = {}
    )
}

@Composable
fun MusicPlayerScreen(
    title: String,
    artist: String,
    position: Long,
    duration: Long,
    isPlaying: Boolean,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onSeek: (Long) -> Unit,
    audiusMusicList: List<AudiusTrack>?,
    onClickMusic: (AudiusTrack) -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn {
            items(audiusMusicList?: emptyList()){
                MusicListComponent(
                    name = it.title?:"",
                    onClickMusic = {
                        onClickMusic(it)
                    }
                )
            }
        }
    }
    /*Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(150.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title.ifEmpty { "No Music Playing" },
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = artist,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Slider(
            value = position.toFloat().coerceIn(
                0f,
                duration.toFloat().coerceAtLeast(1f)
            ),
            onValueChange = { newValue ->
                onSeek(newValue.toLong())
            },
            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
            enabled = duration > 0
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatDuration(position))
            Text(formatDuration(duration))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onPreviousClick) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (isPlaying)
                        Icons.Default.Pause
                    else
                        Icons.Default.PlayArrow,
                    contentDescription = "Play Pause",
                    modifier = Modifier.size(56.dp)
                )
            }

            IconButton(onClick = onNextClick) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }*/
}

fun formatDuration(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d".format(minutes, seconds)
}

@Preview
@Composable
fun MusicListComponentPreview(){
    MusicListComponent(
        name = "Justin",
        onClickMusic = {}
    )
}


@Composable
fun MusicListComponent(
    name: String,
    onClickMusic: () -> Unit
){
    Card(
        onClick = onClickMusic
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name
            )
        }
    }
}