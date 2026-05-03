package com.arestov.playlistmaker.ui.media

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.domain.interactor.ExternalNavigationInteractor
import com.arestov.playlistmaker.domain.interactor.PlaylistInteractor
import com.arestov.playlistmaker.domain.search.interactors.GetTrackHistoryInteractor
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val externalNavigationInteractor: ExternalNavigationInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val getTrackHistoryInteractor: GetTrackHistoryInteractor
) : ViewModel() {

    private val _screenState = MutableLiveData<PlaylistInfoScreenState>()
    val screenStateLiveData: LiveData<PlaylistInfoScreenState> = _screenState

    private var currentPlaylist: Playlist? = null

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylist(playlistId)
            currentPlaylist = playlist

            playlistInteractor.getPlaylistTracks(playlistId).collect { tracks ->
                _screenState.postValue(
                    PlaylistInfoScreenState.Content(playlist, tracks)
                )
            }
        }
    }

    fun deleteTrack(track: Track) {
        val playlist = currentPlaylist ?: return

        viewModelScope.launch {
            playlistInteractor.deletePlaylistTrack(playlist.id, track)
            loadPlaylist(playlist.id)
        }
    }

    suspend fun deletePlaylist() {
        val playlist = currentPlaylist ?: return
        playlistInteractor.deletePlaylist(playlist)
    }

    fun saveTrack(track: Track) {
        getTrackHistoryInteractor.addTrack(track)
    }

    fun sharePlaylist(quantityTrack: String) {
        val playlist = currentPlaylist ?: return

        if (playlist.trackList.isEmpty()) {
            _screenState.postValue(PlaylistInfoScreenState.Toast)
            return
        }

        viewModelScope.launch {
            val builder = StringBuilder()
            builder.append(playlist.name).append("\n")
            if (playlist.description.isNotEmpty()) {
                builder.append(playlist.description).append("\n")
            }
            builder.append("$quantityTrack\n\n")
            playlist.trackList.forEachIndexed { index, trackId ->
                val track = playlistInteractor.getPlaylistTrack(trackId)
                builder.append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeSeconds})\n")
            }

            val shareText = builder.toString()
            externalNavigationInteractor.sharePlaylist(shareText)
        }
    }
}