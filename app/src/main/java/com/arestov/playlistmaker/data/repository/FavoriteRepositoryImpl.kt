package com.arestov.playlistmaker.data.repository

import com.arestov.playlistmaker.data.converters.TrackDbConvertor
import com.arestov.playlistmaker.data.db.FavoriteTrackDatabase
import com.arestov.playlistmaker.data.db.dao.FavoriteTrackDao
import com.arestov.playlistmaker.data.db.entity.TrackEntity
import com.arestov.playlistmaker.domain.repository.FavoriteRepository
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(
    private val trackDao: FavoriteTrackDao,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteRepository {

   override suspend fun addFavoriteTrack(track: Track) {
        val trackEntity = convertFromTrack(track)
        trackDao.insertTracks(trackEntity)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        val trackEntity = convertFromTrack(track)
        trackDao.deleteTracks(trackEntity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = trackDao.getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return trackDao.isFavorite(trackId)
    }

    private fun convertFromTrackEntity(playlistTracksEntity: List<TrackEntity>): List<Track> {
        return playlistTracksEntity.map { playlistTracksEntity -> trackDbConvertor.map(playlistTracksEntity) }
    }

    private fun convertFromTrack(track: Track): TrackEntity {
        return trackDbConvertor.map(track)
    }
}