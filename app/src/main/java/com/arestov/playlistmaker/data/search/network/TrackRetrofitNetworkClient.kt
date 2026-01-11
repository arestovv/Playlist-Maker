import com.arestov.playlistmaker.data.search.model.NetworkResponse
import com.arestov.playlistmaker.data.search.network.RetrofitApi
import com.arestov.playlistmaker.data.search.network.TrackNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRetrofitNetworkClient(
    private val api: RetrofitApi
) : TrackNetworkClient {

    override suspend fun getTracks(text: String): NetworkResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.search(text)
                response.apply { resultCode = 200 }
            } catch (_: Exception) {
                NetworkResponse().apply { resultCode = -1 }
            }
        }
    }
}