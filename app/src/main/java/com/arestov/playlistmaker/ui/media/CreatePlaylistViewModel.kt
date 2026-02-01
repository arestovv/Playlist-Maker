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

    private val _imagePath = MutableLiveData<String>()
    val imagePathLiveData: LiveData<String> = _imagePath

    //Сохранение плейлиста в БД
    fun addPlaylist(name: String, descriptor: String, uri: String) {
        viewModelScope.launch {
            val playlist = Playlist(0, name, descriptor, uri, emptyList(), 0)
            playlistInteractor.addPlaylist(playlist)
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

            _imagePath.postValue(file.absolutePath)
        }
    }
}