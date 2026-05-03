package com.arestov.playlistmaker.ui.media

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.arestov.playlistmaker.domain.search.model.Track
import com.arestov.playlistmaker.ui.search.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistInfoFragment : Fragment() {

    private val viewModel: PlaylistInfoViewModel by viewModel()
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!
    private val args: PlaylistInfoFragmentArgs by navArgs()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var bottomSheetTracks: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetMenu: BottomSheetBehavior<LinearLayout>
    private var toast: Toast? = null

    private var currentPlaylist: Playlist? = null
    private var currentTracks: List<Track> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadPlaylist(args.playlistId)
        setupTrackBottomSheet()
        setupMenuBottomSheet()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(
            data = emptyList(),
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            onItemClick = {
                viewModel.saveTrack(it)
                launchPlayerFragment()
            }
        ).apply {
            setOnItemLongClickListener { track -> showDeleteTrackDialog(track) }
        }

        binding.playlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            buttonBack.setOnClickListener { findNavController().navigateUp() }
            buttonShare.setOnClickListener { viewModel.sharePlaylist(getQuantityTrack()) }
            buttonMenu.setOnClickListener { showMenu() }
            menuShare.setOnClickListener { viewModel.sharePlaylist(getQuantityTrack()) }
            menuEdit.setOnClickListener { launchCreatePlaylistFragment(args.playlistId) }
            menuDelete.setOnClickListener { showDeletePlaylistDialog() }
        }
    }

    private fun observeViewModel() {
        viewModel.screenStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistInfoScreenState.Content -> {
                    currentPlaylist = state.playlist
                    currentTracks = state.tracks
                    showPlaylistInfo(state.playlist, state.tracks)
                }

                is PlaylistInfoScreenState.Toast -> showToast()
                is PlaylistInfoScreenState.Empty -> Unit
            }
        }
    }

    private fun launchCreatePlaylistFragment(id: Int) {
        findNavController().navigate(
            PlaylistInfoFragmentDirections.actionPlaylistInfoFragmentToCreatePlaylistFragment(id)
        )
    }

    private fun showPlaylistInfo(playlist: Playlist, tracks: List<Track>) {
        trackAdapter.updateData(tracks)
        binding.apply {
            if (playlist.imageUri.isNotEmpty()) playlistImage.setImageURI(playlist.imageUri.toUri())
            playlistImage.scaleType = ImageView.ScaleType.CENTER_CROP
            playlistName.text = playlist.name
            playlistDescription.text = playlist.description
            playlistInfo.text = calculateTracks(tracks)
        }
    }

    private fun calculateTracks(tracks: List<Track>): String {
        val totalSeconds = tracks.sumOf {
            it.trackTimeSeconds.split(":").let { parts ->
                val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
                val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
                minutes * 60 + seconds
            }
        }
        val totalMinutes = totalSeconds / 60
        val totalTracks = tracks.size

        val minutesText =
            resources.getQuantityString(R.plurals.minutes_count, totalMinutes, totalMinutes)
        val tracksText =
            resources.getQuantityString(R.plurals.tracks_count, totalTracks, totalTracks)

        return "$minutesText • $tracksText"
    }

    private fun setupTrackBottomSheet() {
        val container = binding.trackBottomSheet
        val overlay = binding.overlayTrack
        val reference = binding.buttonShare

        overlay.visibility = View.GONE
        overlay.alpha = 0f

        bottomSheetTracks = BottomSheetBehavior.from(container).apply {
            isFitToContents = false
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        //высчитываем положение кнопки поделится и подстраиваем боттом шит
        reference.post {
            val location = IntArray(2)
            reference.getLocationOnScreen(location)
            val offset = resources.getDimensionPixelSize(R.dimen.bottom_sheet_extra_offset)
            val contentHeight = location[1] + reference.height + offset
            val screenHeight = resources.displayMetrics.heightPixels

            bottomSheetTracks.peekHeight = screenHeight - contentHeight
            bottomSheetTracks.halfExpandedRatio =
                ((1f - contentHeight / screenHeight.toFloat()).coerceIn(0.1f, 0.9f))
        }

        bottomSheetTracks.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.visibility = if (newState == BottomSheetBehavior.STATE_EXPANDED ||
                    newState == BottomSheetBehavior.STATE_DRAGGING
                ) View.VISIBLE else View.GONE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = (slideOffset * 0.8f).coerceIn(0f, 0.8f)
            }
        })
    }

    private fun setupMenuBottomSheet() {
        val container = binding.menuBottomSheet
        val overlay = binding.overlayMenu

        bottomSheetMenu = BottomSheetBehavior.from(container).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetMenu.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.visibility = if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetTracks.isDraggable = true
                    View.GONE
                } else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        })

        overlay.setOnClickListener { bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN }
    }

    private fun showMenu() {
        bottomSheetTracks.isDraggable = false
        bottomSheetMenu.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            overlayMenu.apply { visibility = View.VISIBLE; alpha = 0f }

            currentPlaylist?.let {
                if (it.imageUri.isNotEmpty()) menuIconAlbum.setImageURI(it.imageUri.toUri())
                menuAlbumName.text = it.name
            }
            menuIconAlbum.scaleType = ImageView.ScaleType.CENTER_CROP
            menuTrackCount.text = getQuantityTrack()
        }
    }

    private fun getQuantityTrack(): String {
        return resources.getQuantityString(
            R.plurals.tracks_count,
            currentTracks.size,
            currentTracks.size
        )
    }


    private fun showDeleteTrackDialog(track: Track) {
        createDialog(getString(R.string.delete_track_title), onPositive = {
            viewModel.deleteTrack(track)
        }).show()
    }

    private fun showDeletePlaylistDialog() {
        createDialog(
            getString(R.string.delete_playlist_title, currentPlaylist?.name),
            onPositive = {
                viewModel.viewModelScope.launch {
                    viewModel.deletePlaylist()
                    findNavController().navigateUp()
                }
            }).show()
    }

    private fun createDialog(title: String, onPositive: () -> Unit): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setNegativeButton(getString(R.string.no_button)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes_button)) { dialog, _ ->
                onPositive.invoke()
                dialog.dismiss()
            }
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_NEGATIVE)?.gravity = Gravity.END
                    getButton(AlertDialog.BUTTON_POSITIVE)?.gravity = Gravity.END
                }
            }
    }

    private fun showToast() {
        toast?.cancel()
        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        layout.findViewById<TextView>(R.id.toast_text).text =
            getString(R.string.playlist_no_shareable_tracks)

        toast = Toast(requireContext()).apply {
            duration = Toast.LENGTH_LONG
            view = layout
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            show()
        }
    }

    private fun launchPlayerFragment() {
        findNavController().navigate(R.id.action_playlistInfoFragment_to_playerFragment)
    }

    override fun onResume() {
        super.onResume()
        if (::bottomSheetMenu.isInitialized) {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}