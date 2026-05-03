package com.arestov.playlistmaker.ui.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arestov.playlistmaker.domain.interactor.PlaylistInteractor
import com.arestov.playlistmaker.domain.search.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

private const val IMAGE_QUALITY = 30
private const val ALBUM_DIR = "myalbum"

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _stateScreen = MutableLiveData<CreatePlaylistScreenState>()
    val stateScreenLiveData: LiveData<CreatePlaylistScreenState> = _stateScreen

    //Сохранение плейлиста в БД
    fun addPlaylist(name: String, descriptor: String, uri: String) {
        viewModelScope.launch {
            val playlist = Playlist(0, name, descriptor, uri, emptyList(), 0)
            playlistInteractor.addPlaylist(playlist)
        }
    }

    suspend fun updatePlaylist(playlistId: Int, name: String, descriptor: String, uri: String) {
        val playlist = playlistInteractor.getPlaylist(playlistId)

        val updatedPlaylist = playlist.copy(
            id = playlistId,
            name = name,
            description = descriptor,
            imageUri = uri,
            trackList = playlist.trackList,
            trackCount = playlist.trackCount
        )

        playlistInteractor.updatePlaylist(updatedPlaylist)
    }

    fun setPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylist(playlistId)
            _stateScreen.postValue(CreatePlaylistScreenState.Content(playlist))
        }
    }

    //Сохранение картинки в память
    fun saveImageToPrivateStorage(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val dir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                ALBUM_DIR
            )
            if (!dir.exists()) dir.mkdirs()

            val file = File(dir, "playlist_${System.currentTimeMillis()}.jpg")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    BitmapFactory.decodeStream(input)
                        .compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, output)
                }
            }

            _stateScreen.postValue(CreatePlaylistScreenState.ImagePath(file.absolutePath))
        }
    }
}