package com.example.mymusicplayer.playerUi.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

sealed class NavigationState: NavKey {

    @Serializable
    data object DashBoardScreen: NavigationState()

    @Serializable
    data object PlayerScreen: NavigationState()
}