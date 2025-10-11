// ========== MusicUtils.kt ==========
package com.example.musicify

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore

data class AudioModel(
    val title: String,
    val artist: String,
    val path: String
)

@SuppressLint("Range")
fun getAllAudioFiles(context: Context): List<AudioModel> {
    val audioList = mutableListOf<AudioModel>()
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ARTIST
    )
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

    try {
        val cursor = context.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        )

        cursor?.use {
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            while (it.moveToNext()) {
                val title = it.getString(titleColumn) ?: "Unknown"
                val path = it.getString(pathColumn) ?: continue
                val artist = it.getString(artistColumn) ?: "Unknown Artist"

                audioList.add(AudioModel(title, artist, path))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return audioList
}

