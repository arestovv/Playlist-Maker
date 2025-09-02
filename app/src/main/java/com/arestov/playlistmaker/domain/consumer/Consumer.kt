package com.arestov.playlistmaker.domain.consumer

interface Consumer<T> {
    fun consume(data: ConsumerData<T>)
}