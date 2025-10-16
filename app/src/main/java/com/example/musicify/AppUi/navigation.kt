package com.example.musicify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicify.AppUi.AlbumsScreen
import com.example.musicify.AppUi.BrowseSongsScreen
import com.example.musicify.AppUi.PlayerScreen
import com.example.musicify.viewmodel.PlayerViewModel

sealed class Screen(val route: String) {
    object Browse : Screen("browse")
    object Albums : Screen("albums")
    object Player : Screen("player/{songId}") {
        fun createRoute(songId: String) = "player/$songId"
    }
}

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: PlayerViewModel = hiltViewModel()) {
    NavHost(navController = navController, startDestination = Screen.Browse.route) {

        composable(Screen.Browse.route) {
            BrowseSongsScreen(
                viewModel = viewModel,
                onNavigateToAlbums = { navController.navigate(Screen.Albums.route) },
                onSongClick = { song ->
                    viewModel.setCurrentSong(song)
                    navController.navigate(Screen.Player.createRoute(song.id))
                }
            )
        }

        composable(Screen.Albums.route) {
            AlbumsScreen(
                onBackClick = { navController.popBackStack() },
                onAlbumClick = { navController.navigate(Screen.Browse.route) }
            )
        }

        composable(Screen.Player.route) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")
            val currentSong = viewModel.currentSong.collectAsState().value
            if (currentSong != null && currentSong.id == songId) {
                PlayerScreen(
                    song = currentSong,

                    onPlayPauseClick = { viewModel.playPause() },
                    onBackClick = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
        }
    }
}
