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
import kotlin.getValue

class CreatePlaylistFragment : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val args: CreatePlaylistFragmentArgs by navArgs()
    private var imageUri: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = args.playlistId
        val flagUpdate = playlistId != -1

        if (flagUpdate) viewModel.setPlaylist(playlistId)

        viewModel.stateScreenLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CreatePlaylistScreenState.Playlist -> {
                    showPlaylistDate(state.playlist)
                }

                is CreatePlaylistScreenState.ImagePath -> {
                    imageUri = state.path
                }
            }

        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.pickerImage.setImageURI(uri)
                    binding.pickerImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    viewModel.saveImageToPrivateStorage(requireContext(), uri)
                }
            }

        binding.pickerImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.namePlaylistInput.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylistButton.isEnabled = !text.isNullOrEmpty()
        }

        //Back
        binding.toolbarCreatePlaylistScreen.setNavigationOnClickListener {
            if (!flagUpdate) {
                showPopupNeedToSave()
            } else {
                findNavController().navigateUp()
            }
        }

        //System back
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!flagUpdate) {
                showPopupNeedToSave()
            } else {
                findNavController().navigateUp()
            }
        }

        //Click button create playlist
        binding.createPlaylistButton.setOnClickListener {
            val name = binding.namePlaylistInput.text.toString()
            val descriptor = binding.descriptionPlaylistInput.text.toString()

            if (flagUpdate) {
                viewModel.viewModelScope.launch {
                    viewModel.updatePlaylist(playlistId, name, descriptor, imageUri)
                    findNavController().navigateUp()
                }
            } else {
                viewModel.addPlaylist(name, descriptor, imageUri)
                showToast()
                findNavController().navigateUp()
            }
        }
    }

    private fun showPlaylistDate(playlist: Playlist) {
        binding.apply {
            pickerImage.setImageURI(playlist.imageUri.toUri())
            pickerImage.scaleType = ImageView.ScaleType.CENTER_CROP
            namePlaylistInput.setText(playlist.name)
            descriptionPlaylistInput.setText(playlist.description)
            createPlaylistButton.setText(getString(R.string.save))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CreatePlaylistFragment()
    }

    // Окно для подтверждения выхода
    private fun createPopupNeedToSave(): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_finish_playlist_title))
            .setMessage(getString(R.string.dialog_finish_playlist_message))
            .setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.dialog_finish) { dialog, _ ->
                findNavController().navigateUp()
            }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let { it.gravity = Gravity.END }
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let { it.gravity = Gravity.END }
        }

        return dialog
    }

    //Показать окно подтверждения при выходе из создания плейлиста
    private fun showPopupNeedToSave() {
        val nameNotEmpty = binding.namePlaylistInput.text.toString().isNotEmpty()
        val descriptionNotEmpty = binding.descriptionPlaylistInput.text.toString().isNotEmpty()
        val imageNotEmpty = imageUri.isNotEmpty()

        if (imageNotEmpty || nameNotEmpty || descriptionNotEmpty) {
            createPopupNeedToSave().show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showToast() {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val textView = layout.findViewById<TextView>(R.id.toast_text)

        textView.text = getString(
            R.string.playlist_created,
            binding.namePlaylistInput.text
        )

        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout

        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        toast.show()
    }
}