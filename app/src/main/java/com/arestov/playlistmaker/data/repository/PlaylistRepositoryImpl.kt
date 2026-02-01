package com.arestov.playlistmaker.data.repository

import com.arestov.playlistmaker.data.converters.PlaylistDbConvertor
import com.arestov.playlistmaker.data.converters.TrackDbConvertor
import com.arestov.playlistmaker.data.db.dao.PlaylistDao
import com.arestov.playlistmaker.data.db.dao.PlaylistTrackDao
import com.arestov.playlistmaker.data.db.entity.PlaylistEntity
import com.arestov.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.arestov.playlistmaker.data.db.entity.TrackEntity
import com.arestov.playlistmaker.domain.repository.PlaylistRepository
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor,
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        val PlaylistEntity = convertFromPlaylist(playlist)
        playlistDao.insertPlaylist(PlaylistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val PlaylistEntity = convertFromPlaylist(playlist)
        playlistDao.deletePlaylist(PlaylistEntity)
    }

    override suspend fun getPlaylist(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists()
            .map { entities -> entities.map { playlistDbConvertor.map(it) } }
    }

    override suspend fun addPlaylistTrack(playlistId: Int, track: Track) {
        val trackEntity = convertTrack(track)
        playlistTrackDao.insertTrack(trackEntity)

        val playlistEntity = playlistDao.getPlaylist(playlistId).first()

        if (!playlistEntity.trackList.contains(trackEntity.trackId)) {
            val updatedPlaylist = playlistEntity.copy(
                trackList = playlistEntity.trackList + trackEntity.trackId,
                trackCount = playlistEntity.trackCount + 1
            )
            playlistDao.updatePlaylist(updatedPlaylist)
        }
    }

    override suspend fun deletePlaylistTrack(playlistId: Int, track: Track) {
        val trackEntity = convertTrack(track)
        playlistTrackDao.deleteTrack(trackEntity)
        val playlistEntity = playlistDao.getPlaylist(playlistId).first()
        val updatedTrackList = playlistEntity.trackList.filter { it != trackEntity.trackId }
        val updatedPlaylist = playlistEntity.copy(
            trackList = updatedTrackList,
            trackCount = playlistEntity.trackCount - 1
        )
        playlistDao.updatePlaylist(updatedPlaylist)
    }

    override suspend fun hasPlaylistTrack(playlistId: Int, trackId: Long): Boolean {
        val playlistEntity = playlistDao.getPlaylist(playlistId).first()
        return playlistEntity.trackList.contains(trackId)
    }

    private fun convertFromPlaylist(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }

    private fun convertTrack(track: Track): PlaylistTrackEntity {
        return trackDbConvertor.mapToPlaylistTrackEntity(track)
    }
}