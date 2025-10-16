// ========== MainActivity.kt ==========
package com.example.musicify


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import com.example.musicify.AppUi.MusicPlayerApp
import com.example.musicify.viewmodel.PlayerViewModel
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start MusicService
        val serviceIntent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        setContent {
            val playerViewModel: PlayerViewModel = hiltViewModel()

            val controllerReady by playerViewModel.controllerReady.collectAsState()
            if (controllerReady) {
                MusicPlayerApp(viewModel = playerViewModel)
            } else {
                // Show loading while MediaController initializes
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MusicService::class.java))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicAppUI(
    mediaController: MediaController?,
    onPermissionCheck: () -> Unit
) {
    val context = LocalContext.current
    var songs by remember { mutableStateOf<List<AudioModel>>(emptyList()) }
    var currentlyPlayingPath by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // Load songs when permission is granted
    LaunchedEffect(Unit) {
        onPermissionCheck()
        songs = getAllAudioFiles(context)
    }

    // Monitor playback state continuously
    LaunchedEffect(mediaController) {
        mediaController?.let { controller ->
            while (true) {
                isPlaying = controller.isPlaying
                kotlinx.coroutines.delay(100) // Check every 100ms
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Music Player") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (songs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading music...")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(songs) { song ->
                    SongItem(
                        song = song,
                        isCurrentlyPlaying = currentlyPlayingPath == song.path,
                        onClick = {
                            currentlyPlayingPath = song.path
                            Log.d("MusicApp", "Playing: ${song.title}, Path: ${song.path}")
                            mediaController?.let { controller ->
                                Log.d("MusicApp", "MediaController available")
                                val mediaItem = androidx.media3.common.MediaItem.fromUri(song.path)
                                controller.setMediaItem(mediaItem)
                                controller.prepare()
                                controller.play()
                                isPlaying = true
                                Log.d("MusicApp", "Play called, isPlaying: ${controller.isPlaying}")
                            } ?: Log.e("MusicApp", "MediaController is NULL!")
                        },
                        onPlayPause = {
                            mediaController?.let { controller ->
                                if (controller.isPlaying) {
                                    controller.pause()
                                    isPlaying = false
                                } else {
                                    controller.play()
                                    isPlaying = true
                                }
                            }
                        },
                        isPlaying=isPlaying
                    )
                }
            }
        }
    }
}

@Composable
fun SongItem(
    song: AudioModel,
    isCurrentlyPlaying: Boolean,
    onClick: () -> Unit,
    onPlayPause: () -> Unit,
    isPlaying: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentlyPlaying)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCurrentlyPlaying)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrentlyPlaying)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Show play/pause button for currently playing song
            IconButton(
                onClick = {
                    if (isCurrentlyPlaying ) {
                        onPlayPause()
                    } else {
                        onClick()
                    }
                }
            ) {
                Icon(
                    imageVector = if (isCurrentlyPlaying && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isCurrentlyPlaying) "Pause" else "Play",
                    tint = if (isCurrentlyPlaying)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

