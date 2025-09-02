package com.arestov.playlistmaker.mvvm.search.domain.consumer

interface Consumer<T> {
    fun consume(data: ConsumerData<T>)
}