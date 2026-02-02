package com.arestov.playlistmaker.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    lateinit var playlist: Playlist;
    private val screenState = MutableLiveData<PlaylistInfoScreenState>()
    val screenStateLiveData: LiveData<PlaylistInfoScreenState> = screenState

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlist = playlistInteractor.getPlaylist(playlistId)

            playlistInteractor.getPlaylistTracks(playlistId).collect { tracks ->
                screenState.postValue(PlaylistInfoScreenState.Content(playlist, tracks))
            }
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylistTrack(playlist.id, track)
            loadPlaylist(playlist.id)
        }
    }

    suspend fun deletePlaylist() {
        playlistInteractor.deletePlaylist(playlist)
    }

    fun saveTrack(track: Track) {
        getTrackHistoryInteractor.addTrack(track)
    }

    fun sharePlaylist() {
        if (playlist.trackList.isEmpty()) {
            screenState.postValue(PlaylistInfoScreenState.Toast)
        } else {
            share()
        }
    }

    private fun share() {
        viewModelScope.launch {
            val builder = StringBuilder()
            builder.append(playlist.name).append("\n")
            if (playlist.description.isNotEmpty()) {
                builder.append(playlist.description).append("\n")
            }
            builder.append("[${playlist.trackList.size}] треков\n\n")

            playlist.trackList.forEachIndexed { index, trackId ->
                val track = playlistInteractor.getPlaylistTrack(trackId)
                builder.append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeSeconds})\n")
            }

            val shareText = builder.toString()
            externalNavigationInteractor.sharePlaylist(shareText)
        }
    }
}