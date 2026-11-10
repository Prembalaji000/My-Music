package com.example.mymusicplayer.playerUi.compose.dashBoard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.mymusicplayer.model.AudiusTrack
import com.example.mymusicplayer.playerUi.compose.MusicListShimmerComponent

@Preview(showBackground = true)
@Composable
fun AlbumScreenPreview(){
    AlbumScreen(
        isLoading = true,
        audiusAlbumMusicList = emptyList(),
        onClickMusic = {},
    )
}

@Composable
fun AlbumScreen(
    isLoading: Boolean?,
    audiusAlbumMusicList: List<AudiusTrack>?,
    onClickMusic: (AudiusTrack) -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8ECEF))
    ) {
        AlbumComponent(
            musicCover = audiusAlbumMusicList?.firstOrNull()?.coverPhotoUrl,
            albumName = audiusAlbumMusicList?.firstOrNull()?.playList?.playListName,
            userName = audiusAlbumMusicList?.firstOrNull()?.user?.name,
        )
        LazyColumn {
            if (isLoading == true && audiusAlbumMusicList?.isEmpty() == true){
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
                    items = audiusAlbumMusicList ?: emptyList(),
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

@Preview
@Composable
fun AlbumComponentPreview() {
    AlbumComponent(
        musicCover = "",
        albumName = "christmas beat tape vol. 3",
        userName = "BeatmakerKelz",
    )
}


@Composable
fun AlbumComponent(
    musicCover: String?,
    albumName: String?,
    userName: String?
){
    var isLoading by remember(musicCover) {
        mutableStateOf(true)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(150.dp)
                .width(180.dp)
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
            text = albumName.orEmpty(),
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
            text = userName.orEmpty(),
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
    }
}
