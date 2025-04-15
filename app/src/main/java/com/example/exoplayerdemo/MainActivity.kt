package com.example.exoplayerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import com.tpstreams.player.TPStreamsPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TPStreamsPlayer.init("6332n7")

        setContent {
            val context = LocalContext.current
            val player = remember { TPStreamsPlayer(context) }
            var mediaItem by remember { mutableStateOf<MediaItem?>(null) }

            // launch coroutine to fetch MediaItem
            LaunchedEffect(Unit) {
                mediaItem = TPStreamsPlayer.buildMediaItem(
                    assetId = "8rEx9apZHFF",
                    accessToken = "19aa0055-d965-4654-8fce-b804e70a46b0"
                )
            }

            DisposableEffect(Unit) {
                onDispose { player.release() }
            }

            mediaItem?.let {
                player.setMediaItem(it)
                player.prepare()
                player.play()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = {
                            PlayerView(it).apply {
                                this.player = player
                                useController = true
                            }
                        }
                    )
                }
            }
        }
    }
}