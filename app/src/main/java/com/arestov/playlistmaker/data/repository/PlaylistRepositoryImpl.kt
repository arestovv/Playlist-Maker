package com.arestov.playlistmaker.data.repository

import com.arestov.playlistmaker.data.converters.PlaylistDbConvertor
import com.arestov.playlistmaker.data.converters.TrackDbConvertor
import com.arestov.playlistmaker.data.db.dao.FavoriteTrackDao
import com.arestov.playlistmaker.data.db.dao.PlaylistDao
import com.arestov.playlistmaker.data.db.dao.PlaylistTrackDao
import com.arestov.playlistmaker.data.db.entity.PlaylistEntity
import com.arestov.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.arestov.playlistmaker.domain.repository.PlaylistRepository
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.Long
import kotlin.collections.List
import kotlin.collections.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val favoriteTrackDao: FavoriteTrackDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor,
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        val PlaylistEntity = convertFromPlaylistToPlaylistEntity(playlist)
        playlistDao.insertPlaylist(PlaylistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val PlaylistEntity = convertFromPlaylistToPlaylistEntity(playlist)
        playlistDao.updatePlaylist(PlaylistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val PlaylistEntity = convertFromPlaylistToPlaylistEntity(playlist)
        val tracks = getPlaylistTracks(PlaylistEntity.id).first()
        playlistDao.deletePlaylist(PlaylistEntity)

        val playlistList = getPlaylists().first()
        for (track in tracks) {
            for (playlist in playlistList) {
                if (playlist.trackList.contains(track.trackId)) {
                    return
                } else {
                    playlistTrackDao.deleteTrack(convertFromTrackToPlaylistTrackEntity(track))
                }
            }
        }
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists()
            .map { entities -> entities.map { playlistDbConvertor.map(it) } }
    }

    override suspend fun getPlaylist(id: Int): Playlist {
        val playlistEntity = playlistDao.getPlaylist(id)
        return convertFromPlaylistEntityToPlaylist(playlistEntity)
    }

    override suspend fun addPlaylistTrack(playlistId: Int, track: Track) {
        val trackEntity = convertFromTrackToPlaylistTrackEntity(track)
        playlistTrackDao.insertTrack(trackEntity)

        val playlistEntity = playlistDao.getPlaylist(playlistId)

        if (!playlistEntity.trackList.contains(trackEntity.trackId)) {
            val updatedPlaylist = playlistEntity.copy(
                trackList = playlistEntity.trackList + trackEntity.trackId,
                trackCount = playlistEntity.trackCount + 1
            )
            playlistDao.updatePlaylist(updatedPlaylist)
        }
    }

    override suspend fun deletePlaylistTrack(playlistId: Int, track: Track) {
        val trackEntity = convertFromTrackToPlaylistTrackEntity(track)
        val playlistEntity = playlistDao.getPlaylist(playlistId)
        val updatedTrackList = playlistEntity.trackList.filter { it != trackEntity.trackId }

        val trackCount = if (playlistEntity.trackCount > 0) playlistEntity.trackCount - 1 else 0
        val updatedPlaylist = playlistEntity.copy(
            trackList = updatedTrackList,
            trackCount = trackCount
        )
        playlistDao.updatePlaylist(updatedPlaylist)

        val playlistList = getPlaylists().first()
        for (playlist in playlistList) {
            if (playlist.trackList.contains(trackEntity.trackId)) {
                return
            }
        }
        playlistTrackDao.deleteTrack(trackEntity)
    }

    override suspend fun hasPlaylistTrack(playlistId: Int, trackId: Long): Boolean {
        val playlistEntity = playlistDao.getPlaylist(playlistId)
        return playlistEntity.trackList.contains(trackId)
    }

    override suspend fun getPlaylistTracks(playlistId: Int): Flow<List<Track>> = flow {
        val trackIdList = playlistDao.getPlaylist(playlistId).trackList
        val trackList = mutableListOf<Track>()

        for (trackId in trackIdList) {
            val playlistTrackEntity = playlistTrackDao.getTrack(trackId)
            val track = convertFromPlaylistTrackEntityToTrack(playlistTrackEntity)
            track.isFavorite = favoriteTrackDao.isFavorite(trackId)
            trackList.add(track)
        }
        emit(trackList.reversed())
    }

    override suspend fun getPlaylistTrack(trackId: Long): Track {
        val playlistTrackEntity = playlistTrackDao.getTrack(trackId)
        return convertFromPlaylistTrackEntityToTrack(playlistTrackEntity)
    }


    private fun convertFromPlaylistToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }

    private fun convertFromPlaylistEntityToPlaylist(playlistEntity: PlaylistEntity): Playlist {
        return playlistDbConvertor.map(playlistEntity)
    }

    private fun convertFromTrackToPlaylistTrackEntity(track: Track): PlaylistTrackEntity {
        return trackDbConvertor.mapTrackToPlaylistTrackEntity(track)
    }

    private fun convertFromPlaylistTrackEntityToTrack(
        playlistTrackEntity: PlaylistTrackEntity
    ): Track {
        return trackDbConvertor.mapPlaylistTrackEntityToTrack(playlistTrackEntity)
    }
}