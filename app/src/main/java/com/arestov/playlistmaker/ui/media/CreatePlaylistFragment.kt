package com.arestov.playlistmaker.ui.media

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.arestov.playlistmaker.domain.search.model.Playlist
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val args: CreatePlaylistFragmentArgs by navArgs()
    private var imageUri: String = ""

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                setImage(it.toString())
                viewModel.saveImageToPrivateStorage(requireContext(), it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = args.playlistId
        val isUpdate = playlistId != -1
        if (isUpdate) viewModel.setPlaylist(playlistId)

        setupObservers()
        setupUI(isUpdate)
    }

    private fun setupObservers() {
        viewModel.stateScreenLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CreatePlaylistScreenState.Content -> showPlaylistData(state.playlist)
                is CreatePlaylistScreenState.ImagePath -> imageUri = state.path
            }
        }
    }

    private fun setupUI(isUpdate: Boolean) {
        binding.apply {
            // Image picker
            pickerImage.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }

            // Enable button only if name is not empty
            namePlaylistInput.doOnTextChanged { text, _, _, _ ->
                createPlaylistButton.isEnabled = !text.isNullOrEmpty()
            }

            // Toolbar back button
            toolbarCreatePlaylistScreen.setNavigationOnClickListener {
                handleBack(isUpdate)
            }

            // System back
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                handleBack(isUpdate)
            }

            // Create / Save playlist button
            createPlaylistButton.setOnClickListener {
                val name = namePlaylistInput.text.toString()
                val description = descriptionPlaylistInput.text.toString()

                if (isUpdate) {
                    viewModel.viewModelScope.launch {
                        viewModel.updatePlaylist(args.playlistId, name, description, imageUri)
                        findNavController().navigateUp()
                    }
                } else {
                    viewModel.addPlaylist(name, description, imageUri)
                    showToast(name)
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setImage(uri: String) {
        imageUri = uri
        binding.pickerImage.apply {
            setImageURI(uri.toUri())
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun showPlaylistData(playlist: Playlist) {
        binding.apply {
            setImage(playlist.imageUri)
            namePlaylistInput.setText(playlist.name)
            descriptionPlaylistInput.setText(playlist.description)
            createPlaylistButton.setText(R.string.save)
        }
    }

    private fun handleBack(isUpdate: Boolean) {
        if (!isUpdate && hasUnsavedChanges()) {
            createPopupNeedToSave().show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        return binding.namePlaylistInput.text?.isNotEmpty() == true ||
                binding.descriptionPlaylistInput.text?.isNotEmpty() == true ||
                imageUri.isNotEmpty()
    }

    private fun createPopupNeedToSave(): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_finish_playlist_title)
            .setMessage(R.string.dialog_finish_playlist_message)
            .setNegativeButton(R.string.dialog_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.dialog_finish) { _, _ -> findNavController().navigateUp() }
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_NEGATIVE)?.gravity = Gravity.END
                    getButton(AlertDialog.BUTTON_POSITIVE)?.gravity = Gravity.END
                }
            }
    }

    private fun showToast(playlistName: String) {
        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
        layout.findViewById<TextView>(R.id.toast_text).text =
            getString(R.string.playlist_created, playlistName)

        Toast(requireContext()).apply {
            duration = Toast.LENGTH_LONG
            view = layout
            setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CreatePlaylistFragment()
    }
}