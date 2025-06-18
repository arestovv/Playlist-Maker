package com.arestov.playlistmaker.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arestov.playlistmaker.PLAYLIST_MAKER_PREFERENCES
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.search.itunes.ITunesClientApi
import com.arestov.playlistmaker.search.track.Track
import com.arestov.playlistmaker.search.track.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    private var text = ""
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView

    private lateinit var infoContainer: LinearLayout
    private lateinit var infoContainerImage: ImageView
    private lateinit var infoContainerText: TextView
    private lateinit var infoContainerButton: Button

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var history: SearchHistory
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var tracksHistoryAdapter: TrackAdapter

    private var tracks = ArrayList<Track>()
    private lateinit var trackAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //RecyclerView for history tracks
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        history = SearchHistory(sharedPrefs)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        clearHistoryButton = findViewById(R.id.history_clear_button)
        tracksHistoryAdapter = TrackAdapter(history.getTracks()) { track ->
            history.addTrack(track)
            updateHistory()
        }
        historyRecyclerView.adapter = tracksHistoryAdapter


        //RecyclerView for tracks
        trackRecyclerView = findViewById(R.id.recyclerView)
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(tracks) { track ->
            history.addTrack(track)
        }
        trackRecyclerView.adapter = trackAdapter

        //search field
        val searchContainer = findViewById<FrameLayout>(R.id.search_container_search_screen)
        inputEditText = findViewById(R.id.search_view_search_screen)
        clearButton = findViewById(R.id.clear_icon_search_view)

        //Container for info message
        infoContainer = findViewById(R.id.info_container)
        infoContainerImage = findViewById(R.id.info_container_image)
        infoContainerText = findViewById(R.id.info_container_text)
        infoContainerButton = findViewById(R.id.info_container_button)

        //History container
        historyContainer = findViewById(R.id.history_container)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)

        //Back
        val back = findViewById<MaterialToolbar>(R.id.toolbar_search_screen)
        back.setNavigationOnClickListener {
            finish()
        }

        //Clear text
        clearButton.setOnClickListener {
            //clear text
            inputEditText.setText("")
            //Hide keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(clearButton.windowToken, 0)
            //hide tracks or container info
            hideContent()
        }

        //update button
        infoContainerButton.setOnClickListener {
            requestToITunes()
        }

        //Set focus to input when open view
        inputEditText.post {
            inputEditText.requestFocus()
        }

        //Focus listener
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            //Show history if has tracks and input has focus and empty
            shouldShowHistory()
        }

        //Click to search input
        searchContainer.setOnClickListener {
            searchContainer.requestFocus()
            //Show keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchContainer, InputMethodManager.SHOW_IMPLICIT)
        }

        inputEditText.doOnTextChanged { text, _, _, _ ->
            //Show button clear if input has text
            clearButton.isVisible = !text.isNullOrEmpty()
            shouldShowHistory()
        }

        //Save text after write
        inputEditText.doAfterTextChanged { input ->
            text = input.toString()
        }

        //click button Done on keyboard
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    requestToITunes()
                }
            }
            false
        }

        //clear button for close and clear history track
        clearHistoryButton.setOnClickListener {
            history.clear()
            hideHistory()
        }
    }

    //fun for get data from itunes api
    private fun requestToITunes() {
        val searchText = inputEditText.text.toString()
        ITunesClientApi().searchTracks(
            text = searchText,
            onSuccess = { showTracks(it) },
            onEmpty = { showNothingFound() },
            onError = { showNetworkProblem() }
        )
    }

    //Show tracks content
    private fun showTracks(results: List<Track>) {
        tracks.clear()
        tracks.addAll(results)
        trackAdapter.notifyDataSetChanged()

        trackRecyclerView.isVisible = true
        infoContainer.isGone = true
    }

    //Show image, button and text network problem
    private fun showNetworkProblem() {
        infoContainerImage.setImageResource(R.drawable.ic_network_problem)
        infoContainerText.setText(R.string.network_problem)

        trackRecyclerView.isGone = true
        infoContainerButton.isVisible = true
        infoContainer.isVisible = true
    }

    //Show image and text nothing found
    private fun showNothingFound() {
        infoContainerImage.setImageResource(R.drawable.ic_nothing_found)
        infoContainerText.setText(R.string.nothing_found)

        trackRecyclerView.isGone = true
        infoContainerButton.isGone = true
        infoContainer.isVisible = true
    }

    //Hide recyclerView and infoContainer
    private fun hideContent() {
        trackRecyclerView.isGone = true
        infoContainerButton.isGone = true
        infoContainer.isGone = true
    }

    //Show history if list has tracks and input has focus and empty
    private fun shouldShowHistory() {
        if (inputEditText.hasFocus()
            && inputEditText.text.isEmpty()
            && history.hasTracks()
        ) {
            showHistory()
        } else {
            hideHistory()
        }

    }

    //Show history container
    private fun showHistory() {
        updateHistory()
        historyContainer.isVisible = true
    }

    //Update adapter
    private fun updateHistory() {
        tracksHistoryAdapter.notifyDataSetChanged()
    }

    //Hide history container
    private fun hideHistory() {
        historyContainer.isVisible = false
    }

    //Save state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, text)
    }

    //Restore state
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(SEARCH_TEXT, "")
        inputEditText.setText(text)
        clearButton.isVisible = !inputEditText.text.isNullOrEmpty()
        requestToITunes()
    }
}
