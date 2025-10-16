package com.example.musicify.models

data class Album(
    val id: String,           // "1"
    val title: String,        // "After Hours"
    val artist: String,       // "The Weeknd"
    val album: String,        // "" (empty string if unknown)
    val duration: Int,        // 14 (minutes? seconds? depends on your source)
    val year: String
)
