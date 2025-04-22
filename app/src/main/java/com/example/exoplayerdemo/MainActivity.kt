package com.example.exoplayerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.tpstreams.player.TPStreamsPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TPStreamsPlayer.init("6332n7")

        setContent {
            val context = LocalContext.current
            val player = remember {
                TPStreamsPlayer.create(
                    context = context,
                    assetId = "8Ky3yJ2f6ke",
                    accessToken = "acd03746-594d-4177-b1f3-044328f0cc17",
                    shouldAutoPlay = false // manual playback
                )
            }

            DisposableEffect(Unit) {
                onDispose { player.release() }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // ✅ ExoPlayer View
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f),
                    factory = {
                        PlayerView(it).apply {
                            this.player = player
                            useController = true
                        }
                    }
                )

                // ✅ Play Button
                Button(
                    onClick = { player.play() },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Play Video")
                }
            }
        }
    }
}