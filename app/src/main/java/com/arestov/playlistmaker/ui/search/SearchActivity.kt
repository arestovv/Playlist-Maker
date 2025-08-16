package com.arestov.playlistmaker.ui.search

import GetTrackHistoryUseCase
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.creator.Creator
import com.arestov.playlistmaker.creator.Creator.provideGetTrackHistoryUseCase
import com.arestov.playlistmaker.domain.model.Track
import com.arestov.playlistmaker.domain.consumer.Consumer
import com.arestov.playlistmaker.domain.consumer.ConsumerData
import com.arestov.playlistmaker.ui.main.sharedPrefs
import com.arestov.playlistmaker.utils.Debounce
import com.arestov.playlistmaker.utils.ScreensHolder
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    val handler = Handler(Looper.getMainLooper())
    private var text = ""
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView

    private lateinit var infoContainer: LinearLayout
    private lateinit var infoContainerImage: ImageView
    private lateinit var infoContainerText: TextView
    private lateinit var infoContainerButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var trackHistoryHolder: GetTrackHistoryUseCase
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var tracksHistoryAdapter: TrackAdapter

    private var tracks = ArrayList<Track>()
    private lateinit var trackAdapter: TrackAdapter

    private val getTrackListUseCase = Creator.provideGetTrackListUseCase()

    //Thread for showing the result while typing
    private val getTracksByTapping = Runnable { getTracks() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //KeyBoard state
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        //RecyclerView for history tracks
        trackHistoryHolder = provideGetTrackHistoryUseCase(sharedPrefs)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        clearHistoryButton = findViewById(R.id.history_clear_button)

        //Listener for click on history track
        tracksHistoryAdapter = TrackAdapter(trackHistoryHolder.getTracks()) { track ->
            if (Debounce.isClickAllowed()) {
                trackHistoryHolder.addTrack(track)
                //Open Player screen
                ScreensHolder.Companion.launch(ScreensHolder.Screens.PLAYER, this)
            }
        }
        historyRecyclerView.adapter = tracksHistoryAdapter

        //RecyclerView for tracks
        trackRecyclerView = findViewById(R.id.recyclerView)
        trackRecyclerView.layoutManager = LinearLayoutManager(this)

        //Listener for click on track
        trackAdapter = TrackAdapter(tracks) { track ->
            if (Debounce.isClickAllowed()) {
                trackHistoryHolder.addTrack(track)
                //Open Player screen
                ScreensHolder.Companion.launch(ScreensHolder.Screens.PLAYER, this)
            }
        }
        trackRecyclerView.adapter = trackAdapter

        //Progress bar
        progressBar = findViewById(R.id.progress_bar)

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
            imm.hideSoftInputFromWindow(clearButton.windowToken, 0)
            //hide tracks or container info
            hideContent()
        }

        //update button
        infoContainerButton.setOnClickListener {
            getTracks()
        }

        //Set focus to input when open view
        inputEditText.post {
            inputEditText.requestFocus()
        }

        //Focus listener
        inputEditText.setOnFocusChangeListener { _, _ ->
            //Show history if has tracks and input has focus and empty
            shouldShowHistory()
        }

        //Click to search input
        searchContainer.setOnClickListener {
            searchContainer.requestFocus()
            //Show keyboard
            imm.showSoftInput(searchContainer, InputMethodManager.SHOW_IMPLICIT)
        }

        inputEditText.doOnTextChanged { text, _, _, _ ->
            Debounce.searchDebounce(getTracksByTapping)
            //Show button clear if input has text
            clearButton.isVisible = !text.isNullOrEmpty()
            //Hide track content when input empty
            if (text.isNullOrEmpty()) hideContent()
            //Show history if has tracks and input has focus and empty
            shouldShowHistory()
        }

        //Save text after write
        inputEditText.doAfterTextChanged { input ->
            text = input.toString()
        }

        //clear button for close and clear history track
        clearHistoryButton.setOnClickListener {
            trackHistoryHolder.clear()
            hideHistory()
        }
    }

    override fun onResume() {
        super.onResume()
        updateHistoryAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        Debounce.removeCallbacks(getTracksByTapping)
    }

    //fun for get data from itunes api
    private fun getTracks() {
        val searchText = inputEditText.text.toString()

        if (searchText.isNotEmpty()) {
            //show progress bar
            showProgressBar()
            getTrackListUseCase.execute(
                text = searchText,
                consumer = object : Consumer<List<Track>> {

                    override fun consume(data: ConsumerData<List<Track>>) {
                        handler.post {
                            when (data) {
                                is ConsumerData.Error -> {
                                    showNetworkProblem()
                                }

                                is ConsumerData.Data ->
                                    if (data.data.isEmpty()) {
                                        showNothingFound()
                                    } else {
                                        showTracks(data.data)
                                    }
                            }
                        }
                    }
                }
            )
        }
    }

    //Show progress bar
    private fun showProgressBar() {
        trackRecyclerView.isGone = true
        infoContainerButton.isGone = true
        infoContainer.isGone = true
        progressBar.isVisible = true
    }

    //Show tracks content
    private fun showTracks(results: List<Track>) {
        tracks.clear()
        tracks.addAll(results)
        trackAdapter.notifyDataSetChanged()

        trackRecyclerView.isVisible = true
        infoContainer.isGone = true
        progressBar.isGone = true
    }

    //Show image, button and text network problem
    private fun showNetworkProblem() {
        infoContainerImage.setImageResource(R.drawable.ic_network_problem)
        infoContainerText.setText(R.string.network_problem)

        trackRecyclerView.isGone = true
        infoContainerButton.isVisible = true
        infoContainer.isVisible = true
        progressBar.isGone = true
    }

    //Show image and text nothing found
    private fun showNothingFound() {
        infoContainerImage.setImageResource(R.drawable.ic_nothing_found)
        infoContainerText.setText(R.string.nothing_found)

        trackRecyclerView.isGone = true
        infoContainerButton.isGone = true
        infoContainer.isVisible = true
        progressBar.isGone = true
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
            && trackHistoryHolder.hasTracks()
        ) {
            showHistory()
        } else {
            hideHistory()
        }

    }

    //Show history container
    private fun showHistory() {
        updateHistoryAdapter()
        historyContainer.isVisible = true
    }

    //Update adapter
    private fun updateHistoryAdapter() {
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
        getTracks()
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}