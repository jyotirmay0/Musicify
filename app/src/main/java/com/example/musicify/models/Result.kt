package com.example.musicify.models

data class Result(
    val album_id: String,
    val album_image: String,
    val album_name: String,
    val artist_id: String,
    val artist_idstr: String,
    val artist_name: String,
    val audio: String,
    val audiodownload: String,
    val audiodownload_allowed: Boolean,
    val content_id_free: Boolean,
    val duration: Int,
    val id: String,
    val image: String,
    val license_ccurl: String,
    val name: String,
    val position: Int,
    val prourl: String,
    val releasedate: String,
    val shareurl: String,
    val shorturl: String
)