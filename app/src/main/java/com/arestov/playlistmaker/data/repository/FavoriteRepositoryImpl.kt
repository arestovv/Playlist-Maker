package com.arestov.playlistmaker.data.repository

import com.arestov.playlistmaker.data.converters.TrackDbConvertor
import com.arestov.playlistmaker.data.db.AppDatabase
import com.arestov.playlistmaker.data.db.entity.TrackEntity
import com.arestov.playlistmaker.domain.repository.FavoriteRepository
import com.arestov.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteRepository {

   override suspend fun addFavoriteTrack(track: Track) {
        val trackEntity = convertFromTrack(track)
        appDatabase.trackDao().insertTracks(trackEntity)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        val trackEntity = convertFromTrack(track)
        appDatabase.trackDao().deleteTracks(trackEntity)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return appDatabase.trackDao().isFavorite(trackId)
    }

    private fun convertFromTrackEntity(tracksEntity: List<TrackEntity>): List<Track> {
        return tracksEntity.map { tracksEntity -> trackDbConvertor.map(tracksEntity) }
    }

    private fun convertFromTrack(track: Track): TrackEntity {
        return trackDbConvertor.map(track)
    }
}