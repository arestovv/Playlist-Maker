package com.arestov.playlistmaker

import android.content.Context
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
import com.arestov.playlistmaker.search.ITunesApi
import com.arestov.playlistmaker.search.Track
import com.arestov.playlistmaker.search.TrackAdapter
import com.arestov.playlistmaker.search.TrackResponse
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    private var text = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView

    private lateinit var infoContainer: LinearLayout
    private lateinit var infoContainerImage: ImageView
    private lateinit var infoContainerText: TextView
    private lateinit var infoContainerButton: Button

    private var tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(tracks)
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //RecyclerView for tracks
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        //search field
        val searchContainer = findViewById<FrameLayout>(R.id.search_container_search_screen)
        inputEditText = findViewById(R.id.search_view_search_screen)
        clearButton = findViewById(R.id.clear_icon_search_view)

        //Container for info message
        infoContainer = findViewById(R.id.info_container)
        infoContainerImage = findViewById(R.id.info_container_image)
        infoContainerText = findViewById(R.id.info_container_text)
        infoContainerButton = findViewById(R.id.info_container_button)

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

        //Click to search input
        searchContainer.setOnClickListener {
            searchContainer.requestFocus()
            //Show keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchContainer, InputMethodManager.SHOW_IMPLICIT)
        }

        //Show button clear
        inputEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.isVisible = !text.isNullOrEmpty()
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
    }

    //fun for get data from itunes
    private fun requestToITunes() {
        val searchText = inputEditText.text.toString()
        iTunesService.search(searchText).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                //response from itunes api
                val results = response.body()?.results.orEmpty()
                when {
                    //if success and not empty
                    response.isSuccessful && results.isNotEmpty() -> showTracks(results)
                    //if nothing found
                    response.isSuccessful -> showNothingFound()
                    //if error
                    else -> showNetworkProblem()
                }
            }
            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showNetworkProblem()
            }
        })
    }

    //Show tracks content
    private fun showTracks(results: List<Track>) {
        tracks.clear()
        tracks.addAll(results)
        trackAdapter.notifyDataSetChanged()

        recyclerView.isVisible = true
        infoContainer.isGone = true
    }

    //Show image, button and text network problem
    private fun showNetworkProblem() {
        infoContainerImage.setImageResource(R.drawable.ic_network_problem)
        infoContainerText.setText(R.string.network_problem)

        recyclerView.isGone = true
        infoContainerButton.isVisible = true
        infoContainer.isVisible = true
    }

    //Show image and text nothing found
    private fun showNothingFound() {
        infoContainerImage.setImageResource(R.drawable.ic_nothing_found)
        infoContainerText.setText(R.string.nothing_found)

        recyclerView.isGone = true
        infoContainerButton.isGone = true
        infoContainer.isVisible = true
    }

    //Hide recyclerView and infoContainer
    private fun hideContent() {
        recyclerView.isGone = true
        infoContainerButton.isGone = true
        infoContainer.isGone = true
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
