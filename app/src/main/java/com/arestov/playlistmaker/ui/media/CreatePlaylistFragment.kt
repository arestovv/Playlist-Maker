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
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arestov.playlistmaker.R
import com.arestov.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class CreatePlaylistFragment : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private var imageUri: String = ""
    lateinit var confirmDialog: MaterialAlertDialogBuilder


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

        confirmDialog = createPopupNeedToSave()

        //сохраняем путь до картинки для передачи в БД при создании плейлиста
        viewModel.imagePathLiveData.observe(viewLifecycleOwner) { path ->
            imageUri = path
        }

        //Загрузка картинки из памяти
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    //Устанавливаем картинку во вью
                    binding.pickerImage.setImageURI(uri)
                    binding.pickerImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    //сохраняем картинку в приватную память
                    viewModel.saveImageToPrivateStorage(requireContext(), uri)
                }
            }

        binding.pickerImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        //Если поле "Название" заполнено, тогда кнопка "Создать" активна
        binding.namePlaylistInput.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylistButton.isEnabled = !text.isNullOrEmpty()
        }

        //Back
        binding.toolbarCreatePlaylistScreen.setNavigationOnClickListener {
            showPopupNeedToSave()
        }

        //System back
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showPopupNeedToSave()
        }

        //Click button create playlist
        binding.createPlaylistButton.setOnClickListener {
            val name = binding.namePlaylistInput.text.toString()
            val descriptor = binding.descriptionPlaylistInput.text.toString()
            viewModel.addPlaylist(name, descriptor, imageUri)
            showToast()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CreatePlaylistFragment()
    }

    //Окно для подтверждения выхода
    private fun createPopupNeedToSave(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_finish_playlist_title))
            .setMessage(getString(R.string.dialog_finish_playlist_message))
            .setNeutralButton(R.string.dialog_cancel) { dialog, which ->
            }.setNegativeButton(R.string.dialog_finish) { dialog, which ->
                findNavController().navigateUp()
            }
    }

    //Показать окно подтверждения при выходе из создания плейлиста
    private fun showPopupNeedToSave() {
        val nameNotEmpty = binding.namePlaylistInput.text.toString().isNotEmpty()
        val descriptionNotEmpty = binding.descriptionPlaylistInput.text.toString().isNotEmpty()
        val imageNotEmpty = imageUri.isNotEmpty()

        //Если какой-то поле заполнено показываем confirmDialog
        if (imageNotEmpty || nameNotEmpty || descriptionNotEmpty) {
            confirmDialog.show()
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