package com.example.mymusicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mymusicplayer.playerUi.compose.dashBoard.MusicPlayerRouter
import com.example.mymusicplayer.playerUi.navigation.NavigationScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerRouter()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}
