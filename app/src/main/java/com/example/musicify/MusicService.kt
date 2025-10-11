// ========== MusicService.kt ==========
package com.example.musicify

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class MusicService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    companion object {
        private const val TAG = "MusicService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "music_playback_channel"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")

        // Create notification channel
        createNotificationChannel()

        // Initialize player with audio attributes
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true // Handle audio focus automatically
            )
            .setHandleAudioBecomingNoisy(true) // Pause when headphones disconnected
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        Log.d(TAG, "Playback state: $playbackState")
                        when (playbackState) {
                            Player.STATE_READY -> {
                                Log.d(TAG, "Player ready, isPlaying: ${this@apply.isPlaying}")
                                updateNotification()
                            }
                            Player.STATE_BUFFERING -> Log.d(TAG, "Buffering...")
                            Player.STATE_ENDED -> Log.d(TAG, "Playback ended")
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        Log.d(TAG, "Is playing changed: $isPlaying")
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        Log.e(TAG, "Player error: ${error.message}", error)
                    }
                })

                // Set volume to maximum
                volume = 1.0f
            }

        // Initialize media session
        mediaSession = MediaSession.Builder(this, player!!)
            .setId("MusicSession")
            .build()

        Log.d(TAG, "Player and session initialized")

        // Start foreground service
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows currently playing music"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val title = player?.currentMediaItem?.mediaMetadata?.title?.toString()
            ?: "Music Player"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("Playing music")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.d(TAG, "onGetSession called")
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        player?.let {
            if (!it.isPlaying) {
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        mediaSession?.run {
            player?.release()
            release()
            mediaSession = null
        }
        player = null
        super.onDestroy()
    }
}