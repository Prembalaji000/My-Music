package com.example.mymusicplayer.playerUi.compose.playerScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.mymusicplayer.R
import com.example.mymusicplayer.model.LyricLine
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
        lyrics = emptyList(),
        isLoadingLyric = false,
        error = ""
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
    lyrics: List<LyricLine>,
    isLoadingLyric: Boolean,
    error: String?,
){
    Scaffold(
        containerColor = Color.White,
        topBar = {
            PlayerTopBar(
                onCloseClick = onCloseClick
            )
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
                    onNextClick = onNextClick,
                    lyrics = lyrics,
                    isLoadingLyric = isLoadingLyric,
                    error = error
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
    isFromHome: Boolean? = false,
    onCloseClick: () -> Unit
){
    val bgColor = if (isFromHome == true) Color(0xFFE8ECEF) else Color.White
    val title = if (isFromHome == true) "Playlist" else "Now Playing"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
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
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                )
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
        lyrics = emptyList(),
        isLoadingLyric = false,
        error = ""
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
    lyrics: List<LyricLine>,
    isLoadingLyric: Boolean,
    error: String?,
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
    var isLoading by remember(musicCover) {
        mutableStateOf(true)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 42.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(330.dp)
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .then(
                    if (!isLoading){
                        Modifier
                            .shadow(6.dp, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (musicCover?.isNotEmpty() == true){
                AsyncImage(
                    model = musicCover,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    fallback = painterResource(R.drawable.player_cover),
                    error = painterResource(R.drawable.player_cover),
                    onLoading = {
                        isLoading = true
                    },
                    onSuccess = {
                        isLoading = false
                    },
                    onError = {
                        isLoading = false
                    }
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
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF374151),
            )
        )

        Spacer(modifier = Modifier.padding(2.dp))

        Text(
            text = artiestName.orEmpty(),
            modifier = Modifier.padding(horizontal = 24.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6B7280),
            )
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

        Spacer(modifier = Modifier.padding(6.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        LyricsSection(
            lyrics = lyrics,
            currentPosMs = currentPosMs,
            isLoading = isLoadingLyric,
            error = error,
            onSeek = onSeek
        )

        /*LyricsSection(
            musicName = musicName,
            artiestName = artiestName,
            currentPosMs = currentPosMs,
            onSeek = onSeek
        )*/
    }
}

@Composable
fun LyricsSection(
    lyrics: List<LyricLine>,
    currentPosMs: Long,
    isLoading: Boolean,
    error: String?,
    onSeek: (Long) -> Unit
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val activeIndex = remember(currentPosMs, lyrics) {
        lyrics.indexOfLast { it.timestampMs <= currentPosMs }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF4F0FF))
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = "Lyrics",
                    tint = Color(0xFF654BFF),
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = "Lyrics",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                )
            }

            Icon(
                imageVector = if (isExpanded) {
                    Icons.Rounded.KeyboardArrowUp
                } else {
                    Icons.Rounded.KeyboardArrowDown
                },
                contentDescription = null,
                tint = Color(0xFF654BFF),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFF654BFF)
                )
            }

            error != null -> {
                Text(
                    text = error,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    color = Color.Gray
                )
            }

            lyrics.isEmpty() -> {
                Text(
                    text = "No lyrics available",
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            !isExpanded -> {
                Text(
                    text = lyrics.getOrNull(activeIndex)
                        ?.text.orEmpty(),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF654BFF),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(lyrics) { index, lyric ->
                        val isActive = index == activeIndex

                        Text(
                            text = lyric.text,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSeek(lyric.timestampMs)
                                }
                                .padding(
                                    vertical = 4.dp,
                                    horizontal = 6.dp
                                ),
                            fontFamily = FontFamily(Font(R.font.roboto_regular)),
                            fontSize = if (isActive) 16.sp else 14.sp,
                            fontWeight = if (isActive) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            color = if (isActive) {
                                Color(0xFF654BFF)
                            } else {
                                Color(0xFF6B7280)
                            }
                        )
                    }
                }
            }
        }
    }
}

