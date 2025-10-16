package com.example.musicify.AppUi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.musicify.models.Result
import com.example.musicify.navigation.Routes


import com.example.musicify.viewmodel.PlayerViewModel
import com.google.common.collect.Multimaps.index

// 1. Browse Songs Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSongsScreen(
    viewModel: PlayerViewModel,
    navController: NavController
) {
    val songs by viewModel.songs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Music", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MusicColors.background,
                    titleContentColor = MusicColors.textPrimary
                ),
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        containerColor = MusicColors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Quick Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text("Songs") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.MusicNote,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                FilterChip(
                    selected = false,
                    onClick = { navController.navigate(Routes.Albums) },
                    label = { Text("Albums") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Album,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            // Songs List with bottom padding for MiniPlayer
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp) // Space for MiniPlayer
            ) {
                itemsIndexed(songs) { index, song ->
                    SongItem(
                        song = song,
                        onClick = {
                            viewModel.setCurrentSong(index, song)
                            navController.navigate(Routes.Player)
                        }
                    )
                }
            }
        }

        // ❌ REMOVE MiniPlayer from here - it's now at app level!
    }
}

@Composable
fun SongItem(song: Result, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art Placeholder
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MusicColors.secondary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = MusicColors.textSecondary,
                modifier = Modifier.size(28.dp)
            )
        }

        // Song Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = song.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MusicColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${song.artist_name} • ${song.album_name}",
                fontSize = 14.sp,
                color = MusicColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Duration
        Text(
            text = "${song.duration}",
            fontSize = 14.sp,
            color = MusicColors.textSecondary
        )

        // More Options
        IconButton(onClick = { /* Show options */ }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More",
                tint = MusicColors.textSecondary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun pre(){

}