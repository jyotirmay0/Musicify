package com.example.musicify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.musicify.Rep.MusicRepository
import com.example.musicify.models.Result
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val controllerFuture: ListenableFuture<MediaController>,
    private val repository: MusicRepository
) : ViewModel() {
    private val _currentSongIndex = MutableStateFlow(-1)
    val currentSongIndex = _currentSongIndex.asStateFlow()
    private val _songs = MutableStateFlow<List<Result>>(emptyList())
    val songs = _songs.asStateFlow()
    private var controller: MediaController? = null
    private var queuedMediaItem: MediaItem? = null

    // Playback states
    private val _currentTitle = MutableStateFlow("")
    val currentTitle = _currentTitle.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering = _isBuffering.asStateFlow()
    private val _songEnded = MutableStateFlow(false)
    val songEnded = _songEnded.asStateFlow()

    private val _controllerReady = MutableStateFlow(false)
    val controllerReady = _controllerReady.asStateFlow()

    init {
        controllerFuture.addListener({
            controller = controllerFuture.get()
            setupController()
            _controllerReady.value = true

            // Play any queued media item
            queuedMediaItem?.let {
                playSong(it)
                queuedMediaItem = null
            }
        }, MoreExecutors.directExecutor())
    }

    private fun setupController() {
        controller?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                _isBuffering.value = state == Player.STATE_BUFFERING
                if (state == Player.STATE_ENDED){
                    _isPlaying.value = false
                    playNextSong()
                }
            }

            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                _isPlaying.value = isPlayingNow
            }

            override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
                _currentTitle.value = item?.mediaMetadata?.title?.toString() ?: ""
            }
        })
    }


    // Play by URI + title
    fun playSong(uri: String, title: String) {
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(MediaMetadata.Builder().setTitle(title).build())
            .build()
        playSong(mediaItem)
    }

    // Internal helper
    private fun playSong(mediaItem: MediaItem) {
        controller?.let { ctrl ->
            ctrl.setMediaItem(mediaItem)
            ctrl.prepare()
            ctrl.play()
            ctrl.playWhenReady = true
        } ?: run {
            queuedMediaItem = mediaItem
        }
    }

    fun playPause() {
        controller?.let { ctrl ->
            val shouldPlay = !ctrl.playWhenReady
            ctrl.playWhenReady = shouldPlay
            _isPlaying.value = shouldPlay
        }
    }



    override fun onCleared() {
        controller?.release()
        controller = null
        super.onCleared()
    }




    private var hasLoadedSongs = false
    fun loadSongs() {
        if (hasLoadedSongs) return   //  Skip if already fetched
        hasLoadedSongs = true

        viewModelScope.launch {
            try {
                val response = repository.getTracks()
                _songs.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    // Add in PlayerViewModel.kt
    private val _currentSong = MutableStateFlow<Result?>(null)
    val currentSong = _currentSong.asStateFlow()


    fun setCurrentSongIndex(index: Int) {
        _currentSongIndex.value = index
    }
    fun setCurrentSong(index: Int, song: Result) {
        _currentSong.value = song
        playSong(uri = song.audio, title = song.name)// play immediately
        setCurrentSongIndex(index)

    }
    fun playNextSong() {
        val nextIndex = _currentSongIndex.value + 1
        val songList = _songs.value
        if (nextIndex < songList.size) {
            val nextSong = songList[nextIndex]
            setCurrentSong(nextIndex, nextSong)

        }
    }

    fun playPreviousSong() {

        val prevIndex = _currentSongIndex.value - 1
        val songList = _songs.value
        if (prevIndex >= 0) {
            val prevSong = songList[prevIndex]
            setCurrentSong(prevIndex, prevSong)
        }
    }
}