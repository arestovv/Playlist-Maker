package com.arestov.playlistmaker.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.databinding.FragmentSearchBinding
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.ui.player.PlayerFragment
import com.arestov.playlistmaker.utils.Debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var trackAdapter: TrackAdapter

    private val viewModel: SearchViewModel by viewModel()
    private val searchRunnable = Runnable {
        viewModel.searchTracks(getInputText())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapters() {
        historyAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.addHistoryTrack(track)
            launchPlayerFragment()
        }
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        trackAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.addHistoryTrack(track)
            launchPlayerFragment()
        }

        binding.trackRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun launchPlayerFragment() {
        requireActivity().supportFragmentManager.commit {
            replace(
                R.id.rootFragmentContainerView,
                PlayerFragment.newInstance()
            )
            addToBackStack(PlayerFragment.TAG)
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
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.screenStateLiveData.observe(getViewLifecycleOwner()) { state ->
            when (state) {
                is SearchScreenState.Loading -> showProgressBar()
                is SearchScreenState.Content -> showTracks(state.tracks)
                is SearchScreenState.EmptyResult -> showNothingFound()
                is SearchScreenState.NetworkError -> showNetworkProblem()
                is SearchScreenState.HistoryContent -> {
                    historyAdapter.updateData(state.historyTracks)
                    updateHistoryVisibility()
                }
            }
        }
    }

    private fun updateHistoryVisibility() {
        val hasNotText = binding.inputSearch.text.isEmpty()
        val hasHistory = historyAdapter.itemCount > 0
        binding.historyContainer.isVisible = hasHistory && hasNotText
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
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let { bundle ->
            val savedText = bundle.getString(SEARCH_TEXT, "")
            binding.inputSearch.setText(savedText)
            binding.buttonClearInputSearch.isVisible = savedText.isNotEmpty()
            if (savedText.isNotEmpty()) {
                viewModel.searchTracks(savedText)
            }
        }
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY = ""
        const val TAG = "SearchFragment"
        fun newInstance() = SearchFragment()
    }
}