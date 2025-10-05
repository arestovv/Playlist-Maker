package com.arestov.playlistmaker.domain.search.usecases

import com.arestov.playlistmaker.domain.search.consumer.Consumer
import com.arestov.playlistmaker.domain.search.consumer.ConsumerData
import com.arestov.playlistmaker.domain.search.entity.Resource
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.domain.search.repository.TrackRepository
import java.util.concurrent.Executors

class GetTrackListUseCase(private val trackRepository: TrackRepository) {

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