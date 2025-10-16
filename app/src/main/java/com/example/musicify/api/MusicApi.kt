package com.example.musicify.api





import com.example.musicify.BuildConfig
import com.example.musicify.models.Track
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApi {


    @GET("tracks/")
    suspend fun getTracks(
        @Query("client_id") clientId:String= BuildConfig.CLIENT_ID,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20
    ): Track
}
