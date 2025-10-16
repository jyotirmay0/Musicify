package com.example.musicify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicify.AppUi.AlbumsScreen
import com.example.musicify.AppUi.BrowseSongsScreen
import com.example.musicify.AppUi.PlayerScreen
import com.example.musicify.viewmodel.PlayerViewModel

object Routes {
    const val Browse = "browse"
    const val Albums = "albums"
    const val Player = "player"
}

@Composable
fun AppNavigation(
    viewModel: PlayerViewModel,
    navController: androidx.navigation.NavHostController
) {

    NavHost(navController = navController, startDestination = Routes.Browse) {

        // Browse Screen
        composable(Routes.Browse) {
            BrowseSongsScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        // Albums Screen
        composable(Routes.Albums) {
            AlbumsScreen(navController = navController)
        }

        // Player Screen - No arguments needed!
        // Just collect the current song from ViewModel
        composable(Routes.Player) {
            val currentSong by viewModel.currentSong.collectAsState()

            PlayerScreen(
                song = currentSong, // Pass the song from ViewModel state
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}