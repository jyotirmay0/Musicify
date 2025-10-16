package com.example.musicify.Rep


import com.example.musicify.api.MusicApi
import com.example.musicify.models.Result

import com.example.musicify.models.Track
import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val api: MusicApi
) {
    private val clientId = "YOUR_CLIENT_ID"

    suspend fun getTracks(): List<Result> {
        val response = api.getTracks()
        return response.results
    }
}
