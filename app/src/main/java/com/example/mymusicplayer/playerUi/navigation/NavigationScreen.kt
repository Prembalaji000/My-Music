package com.example.mymusicplayer.playerUi.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.mymusicplayer.playerUi.MusicPlayerViewModel
import com.example.mymusicplayer.playerUi.compose.dashBoard.MusicPlayerRouter
import com.example.mymusicplayer.playerUi.compose.playerScreen.PlayerScreen

@Composable
fun NavigationScreen(
    viewModel: MusicPlayerViewModel = viewModel()
){
    val backStack = rememberNavBackStack(NavigationState.DashBoardScreen)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<NavigationState.DashBoardScreen>{
                MusicPlayerRouter(
                    /*onNavigateToPlayer = {
                        backStack += NavigationState.PlayerScreen
                    }*/
                )
            }
            entry<NavigationState.PlayerScreen>{

            }
        }
    )
}