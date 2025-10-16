package com.example.musicify.AppUi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicify.navigation.AppNavigation
import com.example.musicify.navigation.Routes
import com.example.musicify.viewmodel.PlayerViewModel

object MusicColors {
    val background = Color(0xFF121212)
    val surface = Color(0xFF1E1E1E)
    val primary = Color(0xFF1DB954)
    val secondary = Color(0xFF282828)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFFB3B3B3)
}

@Composable
fun MusicPlayerApp(viewModel: PlayerViewModel) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Get current song state
    val currentSong by viewModel.currentSong.collectAsState()

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MusicColors.background)
        ) {
            // Main Navigation
            AppNavigation(
                viewModel = viewModel,
                navController = navController
            )

            // MiniPlayer - Show on all screens EXCEPT Player screen
            if (currentSong != null && currentRoute != Routes.Player) {
                MiniPlayer(
                    song = currentSong!!,
                    viewModel = viewModel,
                    navController = navController,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}