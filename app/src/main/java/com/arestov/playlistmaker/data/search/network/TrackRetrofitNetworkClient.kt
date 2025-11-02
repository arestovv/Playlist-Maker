import com.arestov.playlistmaker.data.search.model.NetworkResponse
import com.arestov.playlistmaker.data.search.network.RetrofitApi
import com.arestov.playlistmaker.data.search.network.TrackNetworkClient

class TrackRetrofitNetworkClient(
    private val api: RetrofitApi
) : TrackNetworkClient {

    override fun getTracks(text: String): NetworkResponse {
        return try {
            val response = api.search(text).execute()
            val networkResponse = response.body() ?: NetworkResponse()
            networkResponse.apply { resultCode = response.code() }
        } catch (_: Exception) {
            NetworkResponse().apply { resultCode = -1 }
        }
    }
}