package com.arestov.playlistmaker.ui.search

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.creator.Creator
import com.arestov.playlistmaker.databinding.ActivitySearchBinding
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.ui.main.sharedPrefs
import com.arestov.playlistmaker.utils.Debounce
import com.arestov.playlistmaker.utils.ScreensHolder

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var trackAdapter: TrackAdapter

    private val searchRunnable = Runnable {
        viewModel.searchTracks(getInputText())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            Creator.provideSearchViewModelFactory(sharedPrefs)
        ).get(SearchViewModel::class.java)

        setupAdapters()
        setupListeners()
        observeViewModel()
    }

    private fun setupAdapters() {
        historyAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.addHistoryTrack(track)
            ScreensHolder.launch(ScreensHolder.Screens.PLAYER, this)
        }
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = historyAdapter
        }

        trackAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.addHistoryTrack(track)
            ScreensHolder.launch(ScreensHolder.Screens.PLAYER, this)
        }

        binding.trackRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = trackAdapter
        }
    }

    private fun setupListeners() {

        binding.inputSearch.requestFocus()
        showKeyboard(state = true)

        binding.inputSearch.doOnTextChanged { text, _, _, _ ->
            Debounce.searchDebounce(searchRunnable)
            binding.buttonClearInputSearch.isVisible = !text.isNullOrEmpty()
            if (text.isNullOrEmpty()) hideContent()
            updateHistoryVisibility()
        }

        binding.buttonClearInputSearch.setOnClickListener {
            binding.inputSearch.setText(EMPTY)
            showKeyboard(state = false)
            hideContent()
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistoryTrack()
        }

        binding.inputSearch.setOnClickListener {
            binding.inputSearch.requestFocus()
            showKeyboard(state = true)
        }

        binding.toolbarSearchScreen.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.screenStateLiveData.observe(this) { state ->
            when (state) {
                is SearchScreenState.Loading -> showProgressBar()
                is SearchScreenState.Content -> showTracks(state.tracks)
                is SearchScreenState.EmptyResult -> showNothingFound()
                is SearchScreenState.NetworkError -> showNetworkProblem()
            }
        }

        viewModel.historyTracksLiveData.observe(this) { history ->
            historyAdapter.updateData(history)
            updateHistoryVisibility()
        }
    }

    private fun updateHistoryVisibility() {
        val hasFocus = binding.inputSearch.hasFocus()
        val hasNotText = binding.inputSearch.text.isEmpty()
        val hasHistory = viewModel.hasHistoryTracks()
        binding.historyContainer.isVisible = hasFocus && hasHistory && hasNotText
    }

    private fun hideContent() {
        binding.apply {
            trackRecyclerView.isGone = true
            infoContainer.isGone = true
            infoContainerButton.isGone = true
        }
    }

    private fun showProgressBar() {
        binding.apply {
            trackRecyclerView.isGone = true
            infoContainer.isGone = true
            infoContainerButton.isGone = true
            progressBar.isVisible = true
        }
    }

    private fun showTracks(tracks: List<Track>) {
        trackAdapter.updateData(tracks)
        binding.apply {
            trackRecyclerView.isVisible = true
            infoContainer.isGone = true
            progressBar.isGone = true
        }
    }

    private fun showNothingFound() {
        binding.apply {
            infoContainerImage.setImageResource(R.drawable.ic_nothing_found)
            infoContainerText.setText(R.string.nothing_found)
            trackRecyclerView.isGone = true
            infoContainerButton.isGone = true
            infoContainer.isVisible = true
            progressBar.isGone = true
        }
    }

    private fun showNetworkProblem() {
        binding.apply {
            infoContainerImage.setImageResource(R.drawable.ic_network_problem)
            infoContainerText.setText(R.string.network_problem)
            trackRecyclerView.isGone = true
            infoContainerButton.isVisible = true
            infoContainer.isVisible = true
            progressBar.isGone = true
        }
    }

    private fun showKeyboard(state: Boolean) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (state) imm.showSoftInput(binding.inputSearch, InputMethodManager.SHOW_IMPLICIT)
        else imm.hideSoftInputFromWindow(binding.inputSearch.windowToken, 0)
    }

    private fun getInputText(): String {
        return binding.inputSearch.text.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        Debounce.removeCallbacks(searchRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, getInputText())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedText = savedInstanceState.getString(SEARCH_TEXT, EMPTY)
        binding.inputSearch.setText(savedText)
        binding.buttonClearInputSearch.isVisible = savedText.isNotEmpty()
        if (savedText.isNotEmpty()) {
            viewModel.searchTracks(savedText)
        }
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY = ""
    }
}