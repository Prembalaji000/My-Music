package com.example.mymusicplayer.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mymusicplayer.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


@Preview
@Composable
fun NetworkErrorBarPreview(){
    NetworkErrorBar()
}

@Composable
fun NetworkErrorBar(modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(Color(0xFFF14F4F)),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "No Internet Connection",
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.roboto_regular)),
            fontWeight = FontWeight(400)
        )
    }
}

fun getCurrentConnectivityState(connectivityManager: ConnectivityManager): NetworkConnectionState {
    val network = connectivityManager.activeNetwork

    val isConnected = connectivityManager
        .getNetworkCapabilities(network)
        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

    return if (isConnected) NetworkConnectionState.Available else NetworkConnectionState.Unavailable
}

private fun networkCallback(callback: (NetworkConnectionState) -> Unit): ConnectivityManager.NetworkCallback =
    object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(NetworkConnectionState.Available)
        }

        override fun onLost(network: Network) {
            callback(NetworkConnectionState.Unavailable)
        }
    }

fun Context.observeConnectivityAsFlow(): Flow<NetworkConnectionState> = callbackFlow {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val callback = networkCallback { connectionState ->
        trySend(connectionState)
    }

    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    connectivityManager.registerNetworkCallback(networkRequest, callback)

    val currentState = getCurrentConnectivityState(connectivityManager)
    trySend(currentState)

    awaitClose {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}


val Context.currentConnectivityState: NetworkConnectionState
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return getCurrentConnectivityState(connectivityManager)
    }


sealed interface NetworkConnectionState {
    data object Available : NetworkConnectionState
    data object Unavailable : NetworkConnectionState
}

@Composable
fun rememberConnectivityState(): androidx.compose.runtime.State<NetworkConnectionState> {
    val context = LocalContext.current

    return produceState(initialValue = context.currentConnectivityState) {
        context.observeConnectivityAsFlow().collect {
            value = it
        }
    }
}