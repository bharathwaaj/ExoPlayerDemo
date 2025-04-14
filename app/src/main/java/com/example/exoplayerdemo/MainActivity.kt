package com.example.exoplayerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.exoplayerdemo.ui.theme.ExoPlayerDemoTheme

class MainActivity : ComponentActivity() {
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 🔹 Set up ExoPlayer
        player = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        enableEdgeToEdge()
        setContent {
            ExoPlayerDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    VideoPlayer(player = player, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExoPlayerDemoTheme {
        Greeting("Android")
    }
}