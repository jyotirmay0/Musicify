package com.example.musicify.AppUi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.musicify.models.Result
import com.example.musicify.navigation.AppNavGraph
import com.example.musicify.navigation.Screen
import com.example.musicify.viewmodel.PlayerViewModel

object MusicColors {
    val background = Color(0xFF121212)
    val surface = Color(0xFF1E1E1E)
    val primary = Color(0xFF1DB954) // Spotify green
    val secondary = Color(0xFF282828)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFFB3B3B3)
}
@Composable

fun MusicPlayerApp(viewModel: PlayerViewModel) {
    val navController = rememberNavController()

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = MusicColors.background,
            surface = MusicColors.surface,
            primary = MusicColors.primary,
            onPrimary = Color.White,
            onBackground = MusicColors.textPrimary,
            onSurface = MusicColors.textPrimary
        )
    ) {
        Box(modifier = Modifier.fillMaxSize().background(MusicColors.background)) {
            AppNavGraph(navController = navController, viewModel = viewModel)

            // MiniPlayer
            val currentSong by viewModel.currentSong.collectAsState()
            val isPlaying by viewModel.isPlaying.collectAsState()
            if (currentSong != null) {
                MiniPlayer(
                    song = currentSong!!,
                    isPlaying = isPlaying,
                    onPlayPauseClick = { viewModel.playPause() },
                    onClick = { navController.navigate(Screen.Player.createRoute(currentSong!!.id)) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

