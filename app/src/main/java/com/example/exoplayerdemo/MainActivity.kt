package com.example.exoplayerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import com.tpstreams.player.TPStreamsPlayer
import com.example.exoplayerdemo.ui.theme.ExoPlayerDemoTheme
import android.widget.FrameLayout
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Init TPStreams SDK once (global)
        TPStreamsPlayer.init("6332n7")

        // ✅ Build MediaItem using asset ID and access token
        val mediaItem: MediaItem = TPStreamsPlayer.buildMediaItem(
            assetId = "8Ky3yJ2f6ke",
            accessToken = "acd03746-594d-4177-b1f3-044328f0cc17"
        )

        setContent {
            ExoPlayerDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val player = remember { TPStreamsPlayer(context) }

                    DisposableEffect(Unit) {
                        player.setMediaItem(mediaItem)
                        player.prepare()
                        player.play()

                        onDispose {
                            player.release()
                        }
                    }

                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PlayerView(ctx).apply {
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