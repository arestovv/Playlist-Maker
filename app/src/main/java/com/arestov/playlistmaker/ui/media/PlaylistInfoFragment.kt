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
import kotlin.getValue

class PlaylistInfoFragment : Fragment() {
    private val viewModel: PlaylistInfoViewModel by viewModel()
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var bottomSheetTracks: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetMenu: BottomSheetBehavior<LinearLayout>
    private val args: PlaylistInfoFragmentArgs by navArgs()
    private var toast: Toast? = null
    private var currentPlaylist: Playlist? = null
    private var currentTracks: List<Track> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadPlaylist(args.playlistId)
        setupTrackBottomSheet()
        setupMenuBottomSheet()

        trackAdapter = TrackAdapter(
            data = emptyList(),
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            onItemClick = { track ->
                viewModel.saveTrack(track)
                launchPlayerFragment()
            })

        //Back
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonShare.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.buttonMenu.setOnClickListener {
            showMenu()
        }

        trackAdapter.setOnItemLongClickListener { track ->
            showDeleteDialog(track)
        }

        binding.playlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }

        binding.menuShare.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.menuEdit.setOnClickListener {
            launchCreatePlaylistFragment(args.playlistId)
        }

        binding.menuDelete.setOnClickListener {
            showDeleteDialog()
        }

        viewModel.screenStateLiveData.observe(getViewLifecycleOwner()) { state ->

            when (state) {
                is PlaylistInfoScreenState.Content -> {
                    currentPlaylist = state.playlist
                    currentTracks = state.tracks
                    showPlaylistInfo(state.playlist, state.tracks)
                }

                is PlaylistInfoScreenState.Toast -> {
                    showToast()
                }

                is PlaylistInfoScreenState.Empty -> {
                }
            }
        }
    }

    private fun launchCreatePlaylistFragment(id: Int) {
        val action =
            PlaylistInfoFragmentDirections.actionPlaylistInfoFragmentToCreatePlaylistFragment(id)
        findNavController().navigate(action)
    }

    fun showPlaylistInfo(playlist: Playlist, tracks: List<Track>) {
        trackAdapter.updateData(tracks)

        binding.apply {
            if (!playlist.imageUri.isEmpty()) {
                playlistImage.setImageURI(playlist.imageUri.toUri())
            }

            playlistDescription.text = playlist.description
            playlistImage.scaleType = ImageView.ScaleType.CENTER_CROP
            playlistName.text = playlist.name
            playlistInfo.text = calculateTracks(tracks)
        }
    }


    fun calculateTracks(tracks: List<Track>): String {
        val totalSeconds = tracks.sumOf { track ->
            val parts = track.trackTimeSeconds.split(":")
            if (parts.size == 2) {
                val minutes = parts[0].toIntOrNull() ?: 0
                val seconds = parts[1].toIntOrNull() ?: 0
                minutes * 60 + seconds
            } else 0
        }
        val totalMinutes = totalSeconds / 60
        val totalTracks = tracks.size

        val minutesText = resources.getQuantityString(
            R.plurals.minutes_count,
            totalMinutes,
            totalMinutes
        )

        val tracksText = resources.getQuantityString(
            R.plurals.tracks_count,
            totalTracks,
            totalTracks
        )

        return "$minutesText • $tracksText"
    }

    fun setupTrackBottomSheet() {
        val bottomSheetContainer = binding.trackBottomSheet
        val overlay = binding.overlayTrack
        val lastElement = binding.buttonShare

        overlay.visibility = View.GONE
        overlay.alpha = 0f

        bottomSheetTracks = BottomSheetBehavior.from(bottomSheetContainer).apply {
            isFitToContents = false
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        lastElement.post {
            val location = IntArray(2)
            lastElement.getLocationOnScreen(location)

            val textBottomY = location[1] + lastElement.height
            val screenHeight = resources.displayMetrics.heightPixels
            val offset = resources.getDimensionPixelSize(R.dimen.bottom_sheet_extra_offset)

            val contentHeight = textBottomY + offset

            bottomSheetTracks.peekHeight = screenHeight - contentHeight

            val ratio = contentHeight.toFloat() / screenHeight.toFloat()
            bottomSheetTracks.halfExpandedRatio = (1.0f - ratio).coerceIn(0.1f, 0.9f)
        }

        bottomSheetTracks.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                overlay.visibility = if (newState == BottomSheetBehavior.STATE_EXPANDED ||
                    newState == BottomSheetBehavior.STATE_DRAGGING
                ) {
                    View.VISIBLE
                } else {
                    if (bottomSheetTracks.state == BottomSheetBehavior.STATE_COLLAPSED) View.GONE
                    else View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset <= 0f) {
                    overlay.alpha = 0f
                } else {
                    overlay.alpha = slideOffset * 0.8f
                }
            }
        })
    }

    fun setupMenuBottomSheet() {
        val bottomSheetContainer = binding.menuBottomSheet
        val overlay = binding.overlayMenu

        bottomSheetMenu = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
        }

        bottomSheetMenu.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                        bottomSheetTracks.isDraggable = true
                    }

                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = (slideOffset + 1f) / 1f
                overlay.alpha = alpha.coerceIn(0f, 1f)
            }
        })

        overlay.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showMenu() {
        bottomSheetTracks.isDraggable = false
        binding.overlayMenu.visibility = View.VISIBLE
        binding.overlayMenu.alpha = 0f
        bottomSheetMenu.state = BottomSheetBehavior.STATE_EXPANDED
        if (currentPlaylist?.imageUri?.isNotEmpty() == true) {
            binding.menuIconAlbum.setImageURI(currentPlaylist?.imageUri?.toUri())
        }
        binding.menuIconAlbum.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.menuAlbumName.text = currentPlaylist?.name

        val tracksText = resources.getQuantityString(
            R.plurals.tracks_count,
            currentTracks.size,
            currentTracks.size
        )
        binding.menuTrackCount.text = tracksText
    }

    private fun showDeleteDialog(track: Track) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track_title))
            .setNegativeButton(getString(R.string.no_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes_button)) { dialog, _ ->
                viewModel.deleteTrack(track)
                dialog.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let { it.gravity = Gravity.END }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let { it.gravity = Gravity.END }
        }

        dialog.show()
    }

    private fun showDeleteDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_title, currentPlaylist?.name))
            .setNegativeButton(getString(R.string.no_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes_button)) { dialog, _ ->
                viewModel.viewModelScope.launch {
                    viewModel.deletePlaylist()
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
            }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let { it.gravity = Gravity.END }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let { it.gravity = Gravity.END }
        }

        dialog.show()
    }

    private fun showToast() {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val textView = layout.findViewById<TextView>(R.id.toast_text)
        textView.text = getString(R.string.playlist_no_shareable_tracks)
        toast?.cancel()

        toast = Toast(requireContext()).apply {
            duration = Toast.LENGTH_LONG
            view = layout
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        }
        toast?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchPlayerFragment() {
        findNavController().navigate(R.id.action_playlistInfoFragment_to_playerFragment)
    }

}


