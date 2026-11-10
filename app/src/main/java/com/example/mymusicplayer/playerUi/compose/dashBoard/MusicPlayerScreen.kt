package com.example.mymusicplayer.playerUi.compose.dashBoard

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.mymusicplayer.R
import com.example.mymusicplayer.Utils.NetworkConnectionState
import com.example.mymusicplayer.Utils.NetworkErrorBar
import com.example.mymusicplayer.Utils.rememberConnectivityState
import com.example.mymusicplayer.model.AudiusAlbum
import com.example.mymusicplayer.model.AudiusTrack
import com.example.mymusicplayer.playerUi.MusicListState
import com.example.mymusicplayer.playerUi.compose.AlbumShimmerComponent
import com.example.mymusicplayer.playerUi.compose.MusicListShimmerComponent
import com.example.mymusicplayer.playerUi.UiScreenState
import com.example.mymusicplayer.playerUi.compose.playerScreen.PlayerTopBar

@Preview
@Composable
fun MusicPlayerScreenPreview(){
    MusicPlayerScreen(
        isLoading = true,
        audiusMusicList = null,
        audiusAlbumMusicList = null,
        currentTrack = null,
        onClickMusic = {_, _ ->},
        onClickPlayer = {},
        topAlbum = emptyList(),
        onAlbumClick = {},
        uiState = UiScreenState.DEFAULT_VIEW,
        onChangeUiState = {},
        searchValue = "",
        onSearchValueEntered = {},
        isSearchLoader = false,
        searchAudiusList = emptyList(),
        trackState = MusicListState.HOME_AUDIUS_LIST
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(
    isLoading: Boolean?,
    audiusMusicList: List<AudiusTrack>?,
    audiusAlbumMusicList: List<AudiusTrack>?,
    currentTrack: AudiusTrack?,
    onClickMusic: (AudiusTrack?, MusicListState?) -> Unit,
    onClickPlayer: () -> Unit,
    topAlbum: List<AudiusAlbum>?,
    onAlbumClick: (String) -> Unit,
    uiState: UiScreenState,
    onChangeUiState: (UiScreenState) -> Unit,
    searchValue: String,
    onSearchValueEntered: (String) -> Unit,
    isSearchLoader: Boolean?,
    searchAudiusList: List<AudiusTrack>?,
    trackState: MusicListState?
){

    var sheetState by remember { mutableStateOf(false) }
    val homeModelBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            newValue != SheetValue.Hidden
        }
    )

    if (sheetState) {
        ModalBottomSheet(
            sheetState = homeModelBottomSheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            tonalElevation = 8.dp,
            containerColor = Color(0xFFE8ECEF),
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.96f)
                ){
                    SearchScreen(
                        searchValue = searchValue,
                        onSearchValueEntered = onSearchValueEntered,
                        onClose = {
                            sheetState = false
                        },
                        isSearchLoader = isSearchLoader,
                        searchAudiusList = searchAudiusList,
                        onClickMusic = {
                            onClickMusic(it, MusicListState.SEARCH_AUDIUS_LIST)
                        }
                    )
                }
            },
            onDismissRequest = {
                sheetState = false
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFE8ECEF),
        topBar = {
            if (uiState == UiScreenState.DEFAULT_VIEW){
                MusicScreenTopBar(
                    onSearchClick = {
                        sheetState = true
                    }
                )
            } else {
                PlayerTopBar(
                    isFromHome = true,
                    onCloseClick = {
                        onChangeUiState(UiScreenState.DEFAULT_VIEW)
                    }
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFE8ECEF))
            ) {
                Column {
                    when (uiState){
                        UiScreenState.DEFAULT_VIEW -> {
                            PopularAlbum(
                                isLoading = isLoading,
                                topAlbum = topAlbum,
                                onAlbumClick = {
                                    onAlbumClick(it)
                                    onChangeUiState(UiScreenState.ALBUM_VIEW)
                                }
                            )

                            Spacer(modifier = Modifier.padding(6.dp))

                            Text(
                                modifier = Modifier
                                    .padding(14.dp),
                                text = "Music List",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF374151),
                                )
                            )

                            LazyColumn {
                                if (isLoading == true){
                                    item {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 24.dp, vertical = 8.dp)
                                        ) {
                                            repeat(5){
                                                MusicListShimmerComponent()
                                                Spacer(modifier = Modifier.padding(2.dp))
                                            }
                                        }
                                    }
                                } else {
                                    items(
                                        items = audiusMusicList ?: emptyList(),
                                        key = { it.id }
                                    ) { track ->
                                        MusicListComponent(
                                            name = track.title.orEmpty(),
                                            artiestName = track.artistName,
                                            time = formatDuration((track.duration?.toLong() ?: 0L) * 1000L),
                                            onClickMusic = {
                                                onClickMusic(track, MusicListState.HOME_AUDIUS_LIST)
                                            },
                                            isPlaying = track.isPlaying
                                        )
                                    }
                                }
                            }
                        }
                        UiScreenState.ALBUM_VIEW -> {
                            AlbumScreen(
                                isLoading = isLoading,
                                audiusAlbumMusicList = audiusAlbumMusicList,
                                onClickMusic = {
                                    onClickMusic(it, MusicListState.ALBUM_AUDIUS_LIST)
                                }
                            )
                        }
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
                    coverImage = currentTrack?.artworkUrl,
                    onPlayPauseClick = {
                        onClickMusic(currentTrack, trackState)
                    },
                    onClickPlayer = onClickPlayer
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
    if (duration == null || duration <= 0) return "00:00"
    val totalSeconds = duration / 1000L

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
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF374151),
                    )
                )

                Text(
                    text = artiestName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF6B7280),
                    )
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
    MusicScreenTopBar(
        onSearchClick = {}
    )
}


@Composable
fun MusicScreenTopBar(
    onSearchClick: () -> Unit
){
    val networkStatus by rememberConnectivityState()

    Column {
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
                        onClick = onSearchClick
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null
                        )
                    }
                    Text(
                        text = "My Music Player",
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
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = null
                        )
                    }
                }
               /* Text(
                    modifier = Modifier
                        .padding(14.dp),
                    text = "Browse",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                    )
                )*/
            }
        }
        AnimatedVisibility(
            visible = networkStatus == NetworkConnectionState.Unavailable,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            NetworkErrorBar()
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
        onPlayPauseClick = {},
        onClickPlayer = {},
    )
}

@Composable
fun PlayerComponent(
    musicName: String?,
    artiestName: String?,
    isPlaying: Boolean?,
    coverImage: String?,
    onPlayPauseClick: () -> Unit,
    onClickPlayer: () -> Unit
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
            .shadow(
                elevation = 6.dp,
            )
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClickPlayer
            )
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
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF374151),
                    )
                )

                Text(
                    text = artiestName.orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF6B7280),
                    )
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

@Preview(showBackground = true)
@Composable
fun PopularAlbumPreview(){
    PopularAlbum(
        topAlbum = emptyList(),
        isLoading = true,
        onAlbumClick = {}
    )
}

@Composable
fun PopularAlbum(
    topAlbum: List<AudiusAlbum>?,
    isLoading: Boolean?,
    onAlbumClick: (String) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .padding(14.dp),
            text = "Popular Album",
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151),
            )
        )

        if (isLoading == true){
            Row(
                modifier = Modifier
                    .padding(start = 14.dp)
            ) {
                repeat(4){
                    AlbumShimmerComponent()
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        } else {
            LazyRow {
                items(topAlbum?:emptyList()){
                    PopularAlbumComponent(
                        albumCover = it.coverPhotoUrl,
                        albumId = it.id,
                        onAlbumClick = onAlbumClick
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PopularAlbumComponentPreview(){
    PopularAlbumComponent(
        albumCover = "",
        albumId = "",
        onAlbumClick = {}
    )
}

@Composable
fun PopularAlbumComponent(
    albumCover: String?,
    albumId: String,
    onAlbumClick: (String) -> Unit
){
    var isLoading by remember(albumCover) {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .padding(start = 14.dp)
            .height(132.dp)
            .width(122.dp)
            .clickable(
                onClick = {
                    onAlbumClick(albumId)
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
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
        if (albumCover?.isNotEmpty() == true){
            AsyncImage(
                model = albumCover,
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
            if (isLoading){
                AlbumShimmerComponent()
            }
        } else {
            Image(
                painter = painterResource(R.drawable.player_cover),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview(){
    SearchScreen(
        searchValue = "",
        onSearchValueEntered = {},
        onClose = {},
        isSearchLoader = false,
        searchAudiusList = emptyList(),
        onClickMusic = {}
    )
}

@Composable
fun SearchScreen(
    searchValue: String,
    onSearchValueEntered: (String) -> Unit,
    onClose: () -> Unit,
    isSearchLoader: Boolean?,
    searchAudiusList: List<AudiusTrack>?,
    onClickMusic: (AudiusTrack) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8ECEF))
    ) {
        SearchBar(
            searchValue = searchValue,
            onSearchValueEntered = onSearchValueEntered,
            onClose = onClose,
            isSearchLoader = isSearchLoader
        )
        LazyColumn {
            if (isSearchLoader == true && searchAudiusList?.isEmpty() == true){
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        repeat(5){
                            MusicListShimmerComponent()
                            Spacer(modifier = Modifier.padding(2.dp))
                        }
                    }
                }
            } else {
                items(
                    items = searchAudiusList ?: emptyList(),
                    key = { it.id }
                ) { track ->
                    MusicListComponent(
                        name = track.title.orEmpty(),
                        artiestName = track.artistName,
                        time = formatDuration((track.duration?.toLong() ?: 0L) * 1000L),
                        onClickMusic = {
                            onClickMusic(track)
                        },
                        isPlaying = track.isPlaying
                    )
                }
            }
        }
    }
}


@Composable
fun SearchBar(
    searchValue: String,
    onSearchValueEntered: (String) -> Unit,
    onClose: () -> Unit,
    isSearchLoader: Boolean?
){
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
            onClick = onClose
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "arrow back",
                tint = colorResource(id = R.color.grey_color)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        OutlinedTextField(
            value = searchValue,
            onValueChange = { value ->
                onSearchValueEntered(value)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = colorResource(id = R.color.buzz_txt_color)
                )
            },
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF9CA3AF)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                onDone = {
                    focusManager.clearFocus(force = true)
                }
            ),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                if (isSearchLoader == true){
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = colorResource(R.color.digi_blue)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(52.dp),
            placeholder = {
                Text(
                    text = "What do you want to listen to?",
                    color = colorResource(R.color.college_code_text_hint),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.roboto_regular)),
                        fontWeight = FontWeight.Normal,
                        color = colorResource(id = R.color.buzz_txt_color)
                    ),
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = colorResource(id = R.color.digi_blue),
                unfocusedIndicatorColor = colorResource(id = R.color.chatly_search_bar_border),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = colorResource(R.color.digi_blue),
            ),
        )
    }
}

