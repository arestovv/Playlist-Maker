import com.arestov.playlistmaker.domain.model.Track
import com.arestov.playlistmaker.domain.repository.TrackHistoryRepository

class GetTrackHistoryUseCase(
    private val trackHistoryRepository: TrackHistoryRepository
) {
    fun getTracks(): List<Track> = trackHistoryRepository.getTracks()
    fun addTrack(track: Track) = trackHistoryRepository.addTrack(track)
    fun hasTracks() = trackHistoryRepository.hasTracks()
    fun clear() = trackHistoryRepository.clear()
}