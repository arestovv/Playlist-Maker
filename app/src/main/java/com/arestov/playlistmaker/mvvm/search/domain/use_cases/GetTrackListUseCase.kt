import com.arestov.playlistmaker.mvvm.search.domain.consumer.Consumer
import com.arestov.playlistmaker.mvvm.search.domain.consumer.ConsumerData
import com.arestov.playlistmaker.mvvm.search.domain.entity.Resource
import com.arestov.playlistmaker.mvvm.search.domain.model.Track
import com.arestov.playlistmaker.mvvm.search.domain.repository.TrackRepository
import java.util.concurrent.Executors

class GetTrackListUseCase(
    private val trackRepository: TrackRepository
) {

    private val executor = Executors.newSingleThreadExecutor()

    fun execute(text: String, consumer: Consumer<List<Track>>) {
        executor.execute {
            val resource = trackRepository.getTracks(text)

            when (resource) {
                is Resource.Success -> {
                    consumer.consume(ConsumerData.Data(resource.data))
                }
                is Resource.Error -> {
                    consumer.consume(ConsumerData.Error(resource.message))
                }
            }
        }
    }
}