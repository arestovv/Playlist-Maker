package com.arestov.playlistmaker.data.search.repository

import com.arestov.playlistmaker.domain.repository.PreferencesStorageRepository
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.domain.search.repository.TrackHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackHistoryRepositoryImpl(private val storage: PreferencesStorageRepository) :
    TrackHistoryRepository {
    private var listTracks = ArrayList<Track>()

    //Get list track from Prefs
    override fun getTracks(): ArrayList<Track> {
        listTracks = getListFromPrefs()
        return listTracks
    }

    //Return is list has track
    override fun hasTracks(): Boolean {
        return listTracks.isNotEmpty()
    }

    //Add track to list with clear logic and save to Prefs
    override fun addTrack(track: Track) {
        val existingIndex = listTracks.indexOf(track)
        //if track exist, remove this track
        if (existingIndex != -1) {
            listTracks.removeAt(existingIndex)
            //if tracks more 10 remove last
        } else if (listTracks.size >= 10) {
            listTracks.removeAt(listTracks.size - 1)
        }
        //add track to list
        listTracks.add(0, track)
        //save track to Prefs
        setListToPrefs(listTracks)
    }

    //Clear list and Pref
    override fun clear() {
        listTracks.clear()
        clearListFromPref()
    }

    //Save tracks list to sharedPreferences
    private fun setListToPrefs(list: List<Track>) {
        val json = Gson().toJson(list)
        storage.putString(json)
    }

    //Get list tracks from SharedPreferences
    private fun getListFromPrefs(): ArrayList<Track> {
        val json = storage.getString() ?: return ArrayList()
        val type = object : TypeToken<List<Track>>() {}.type
        val list: List<Track> = Gson().fromJson(json, type)
        return ArrayList(list)
    }

    //Clear list tracks from SharedPreferences
    private fun clearListFromPref() {
        storage.clear()
    }
}