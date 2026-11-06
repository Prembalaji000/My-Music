package com.example.mymusicplayer

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture

class MusicControllerManager(
    private val context: Context
) {
    private var controllerFeature: ListenableFuture<MediaController>? = null

    fun connect(
        onConnected: (MediaController) -> Unit
    ){
        val sessionToken = SessionToken(
            context,
            ComponentName(
                context,
                MusicPlaybackService::class.java
            )
        )

        controllerFeature = MediaController.Builder(
            context,
            sessionToken
        ).buildAsync()

        controllerFeature?.addListener(
            {
                try {
                    val controller = controllerFeature?.get()
                    controller?.let(onConnected)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }


    fun release() {
        controllerFeature?.let {
            MediaController.releaseFuture(it)
        }

        controllerFeature = null
    }
}