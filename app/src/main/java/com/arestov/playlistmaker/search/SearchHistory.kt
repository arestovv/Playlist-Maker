package com.arestov.playlistmaker.search

import android.content.SharedPreferences
import com.arestov.playlistmaker.search.track.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val TRACK_HISTORY_KEY = "track_history"

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private var listTracks = ArrayList<Track>()

    //Get list track from Prefs
    fun getTracks(): ArrayList<Track> {
        listTracks = getListFromPrefs()
        return listTracks
    }

    //Return is list has track
    fun hasTracks(): Boolean {
        return listTracks.size > 0
    }

    //Add track to list with clear logic and save to Prefs
    fun addTrack(track: Track) {
        val existingIndex = listTracks.indexOf(track)
        //if track exist, remove this track
        if (existingIndex != -1) {
            listTracks.removeAt(existingIndex)
        //if tracks more 10 remove last
        } else if (listTracks.size >= 10){
            listTracks.removeLast()
        }
        //add track to list
        listTracks.add(0, track)
        //save track to Prefs
        setListToPrefs(listTracks)
    }

    //Clear list and Pref
    fun clear() {
        listTracks.clear()
        clearListFromPref()
    }

    //Save tracks list to sharedPreferences
    private fun setListToPrefs(list: List<Track>) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(list)
        editor.putString(TRACK_HISTORY_KEY, json)
        editor.apply()
    }

    //Get list tracks from SharedPreferences
    private fun getListFromPrefs(): ArrayList<Track> {
        val json = sharedPreferences.getString(TRACK_HISTORY_KEY, null)
        val type = object : TypeToken<List<Track>>() {}.type
        return if (json != null)
            Gson().fromJson(json, type)
        else ArrayList()
    }

    //Clear list tracks from SharedPreferences
    private fun clearListFromPref() {
        sharedPreferences.edit()
            .remove(TRACK_HISTORY_KEY)
            .apply()
    }
}