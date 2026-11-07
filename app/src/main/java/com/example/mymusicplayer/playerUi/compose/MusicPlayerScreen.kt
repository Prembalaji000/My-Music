package com.example.mymusicplayer.playerUi.compose

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.crossfade
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
        currentTrack = null,
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
    currentTrack: AudiusTrack?,
    onClickMusic: (AudiusTrack?) -> Unit
){
    Scaffold(
        topBar = {
            MusicScreenTopBar()
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFE8ECEF))
            ) {
                LazyColumn {
                    item {
                        Text(
                            modifier = Modifier
                                .padding(14.dp),
                            text = "Music List",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(
                        items = audiusMusicList ?: emptyList(),
                        key = { it.id }
                    ) { track ->
                        MusicListComponent(
                            name = track.title.orEmpty(),
                            artiestName = track.artistName,
                            time = formatDuration(track.duration?.toLong()?:0),
                            onClickMusic = {
                                onClickMusic(track)
                            },
                            isPlaying = track.isPlaying
                        )
                    }
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(currentTrack != null) {
                PlayerComponent(
                    musicName = currentTrack?.title,
                    artiestName = currentTrack?.artistName,
                    isPlaying = currentTrack?.isPlaying,
                    coverImage = currentTrack?.coverPhotoUrl,
                    onPlayPauseClick = {
                        onClickMusic(currentTrack)
                    }
                )
            }
        }
    )
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

fun formatDuration(duration: Long?): String {
    val totalSeconds = duration ?: 0L

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

@Preview
@Composable
fun MusicListComponentPreview(){
    MusicListComponent(
        name = "Justin",
        artiestName = "Justin",
        time = "2:23",
        onClickMusic = {},
        isPlaying = true
    )
}


@Composable
fun MusicListComponent(
    name: String,
    artiestName: String,
    time: String,
    onClickMusic: () -> Unit,
    isPlaying: Boolean
){
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8673FD),
            Color(0xFF5C42FF)
        )
    )
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        onClick = onClickMusic,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClickMusic
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .then(
                            if (isPlaying){
                                Modifier.background(gradient)
                            } else {
                                Modifier.border(
                                    width = 1.dp,
                                    color = Color(0xFF5C42FF),
                                    shape = CircleShape
                                )
                            }
                        )
                ) {
                    Icon(
                        modifier = Modifier.padding(8.dp),
                        imageVector = if (isPlaying){
                            Icons.Rounded.Pause
                        } else {
                            Icons.Rounded.PlayArrow
                        },
                        contentDescription = null,
                        tint = if (isPlaying) Color.White else Color(0xFF5C42FF)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = artiestName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun MusicScreenTopBarPreview(){
    MusicScreenTopBar()
}


@Composable
fun MusicScreenTopBar(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8ECEF))
            .padding(top = 26.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = null
                    )
                }
            }
            Text(
                modifier = Modifier
                    .padding(14.dp),
                text = "Browse",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview
@Composable
fun PlayerComponentPreview(){
    PlayerComponent(
        musicName = "Different world",
        artiestName = "Travis scott",
        isPlaying = true,
        coverImage = "",
        onPlayPauseClick = {}
    )
}

@Composable
fun PlayerComponent(
    musicName: String?,
    artiestName: String?,
    isPlaying: Boolean?,
    coverImage: String?,
    onPlayPauseClick: () -> Unit
){
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8673FD),
            Color(0xFF5C42FF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF5C42FF),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (coverImage.isNullOrBlank()) {
                    Icon(
                        modifier = Modifier.padding(8.dp),
                        imageVector = Icons.Rounded.Image,
                        contentDescription = null
                    )
                } else {
                    AsyncImage(
                        model = coverImage,
                        contentDescription = "Album artwork",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onError = {
                            Log.e(
                                "AudiusImage",
                                "Image loading failed: ${it.result.throwable}"
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = musicName.orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = artiestName.orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .then(
                        if (isPlaying == true){
                            Modifier.background(gradient)
                        } else {
                            Modifier.border(
                                width = 1.dp,
                                color = Color(0xFF5C42FF),
                                shape = CircleShape
                            )
                        }
                    )
                    .clickable(
                        onClick = onPlayPauseClick
                    )
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = if (isPlaying == true){
                        Icons.Rounded.Pause
                    } else {
                        Icons.Rounded.PlayArrow
                    },
                    contentDescription = null,
                    tint = if (isPlaying == true) Color.White else Color(0xFF5C42FF)
                )
            }
        }
    }
}