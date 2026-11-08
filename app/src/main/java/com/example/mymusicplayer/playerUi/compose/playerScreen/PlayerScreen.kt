package com.example.mymusicplayer.playerUi.compose.playerScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.mymusicplayer.R
import com.example.mymusicplayer.playerUi.compose.dashBoard.formatDuration

@Preview
@Composable
fun PlayerUiScreenPreview(){
    PlayerScreen(
        musicName = "Different world",
        artiestName = "Travis scott",
        musicCover = "",
        position = 0,
        duration = 0,
        onSeek = {},
        currentPosition = 0,
        totalDuration = 245,
        isPlaying = false,
        onCloseClick = {},
        onClickMusic = {},
        onPreviousClick = {},
        onNextClick = {},
    )
}

@Composable
fun PlayerScreen(
    musicName: String?,
    artiestName: String?,
    musicCover: String?,
    position: Long?,
    duration: Long?,
    onSeek: (Long) -> Unit,
    currentPosition: Long?,
    totalDuration: Int?,
    isPlaying: Boolean?,
    onCloseClick: () -> Unit,
    onClickMusic: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
){
    Scaffold(
        containerColor = Color.White,
        topBar = {
            PlayerTopBar(onCloseClick)
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                PlayerComponents(
                    musicName = musicName,
                    artiestName = artiestName,
                    musicCover = musicCover,
                    position = position,
                    duration = duration,
                    onSeek = onSeek,
                    currentPosition = currentPosition,
                    totalDuration = totalDuration,
                    isPlaying = isPlaying,
                    onClickMusic = onClickMusic,
                    onPreviousClick = onPreviousClick,
                    onNextClick = onNextClick
                )
            }
        }
    )
}

@Preview
@Composable
fun PlayerTopBarPreview(){
    PlayerTopBar(
        onCloseClick = {}
    )
}

@Composable
fun PlayerTopBar(
    onCloseClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 26.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onCloseClick
            ) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            Text(
                text = "Now Playing",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                onClick = {}
            ) {
                Icon(
                    Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }
    }
}

@Preview
@Composable
fun PlayerComponentsPreview(){
    PlayerComponents(
        musicName = "Different world",
        artiestName = "Travis scott",
        musicCover = "",
        position = 0,
        duration = 0,
        onSeek = {},
        currentPosition = 0,
        totalDuration = 245,
        isPlaying = false,
        onClickMusic = {},
        onPreviousClick = {},
        onNextClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerComponents(
    musicName: String?,
    artiestName: String?,
    musicCover: String?,
    position: Long?,
    duration: Long?,
    onSeek: (Long) -> Unit,
    currentPosition: Long?,
    totalDuration: Int?,
    isPlaying: Boolean?,
    onClickMusic: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
){
    val totalDurMs = if ((duration ?: 0L) > 0L) {
        duration ?: 0L
    } else {
        (totalDuration?.toLong() ?: 0L) * 1000L
    }

    val currentPosMs = (position ?: currentPosition ?: 0L).coerceIn(0L, totalDurMs.coerceAtLeast(1L))

    var isDragging by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableStateOf(0f) }

    val currentSliderValue = if (isDragging) sliderPosition else currentPosMs.toFloat()
    val maxSliderValue = totalDurMs.toFloat().coerceAtLeast(1f)

    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8673FD),
            Color(0xFF5C42FF)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 42.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(330.dp)
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (musicCover?.isNotEmpty() == true){
                AsyncImage(
                    model = musicCover,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.player_cover),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.padding(12.dp))

        Text(
            text = musicName.orEmpty(),
            modifier = Modifier.padding(horizontal = 24.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            text = artiestName.orEmpty(),
            modifier = Modifier.padding(horizontal = 24.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.padding(12.dp))

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 38.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val purple = Color(0xFF654BFF)

            Slider(
                value = currentSliderValue.coerceIn(0f, maxSliderValue),
                onValueChange = { newValue ->
                    isDragging = true
                    sliderPosition = newValue
                },
                valueRange = 0f..maxSliderValue,
                interactionSource = interactionSource,
                onValueChangeFinished = {
                    onSeek(sliderPosition.toLong())
                    isDragging = false
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(6.dp),
                        colors = SliderDefaults.colors(
                            activeTrackColor = purple,
                            inactiveTrackColor = Color(0xFFE5E5EA)
                        ),
                        thumbTrackGapSize = 0.dp,
                        trackInsideCornerSize = 0.dp
                    )
                },
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .shadow(
                                elevation = 3.dp,
                                shape = CircleShape
                            )
                            .background(
                                color = purple,
                                shape = CircleShape
                            )
                    )
                }
            )

            Spacer(modifier = Modifier.padding(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(currentSliderValue.toLong()),
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Text(
                    text = formatDuration(totalDurMs),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onPreviousClick
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = onClickMusic,
                modifier = Modifier.size(52.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(gradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying == true) {
                            Icons.Rounded.Pause
                        } else {
                            Icons.Rounded.PlayArrow
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = onNextClick
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = null
                )
            }
        }
    }
}