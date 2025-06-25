package com.example.plantpal.ui

import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs


import java.io.File
import androidx.core.net.toUri
import com.example.plantpal.FavoriteViewModel
import com.example.plantpal.Plant
import com.example.plantpal.R
import com.example.plantpal.databinding.FragmentAddEditPlantBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditPlantFragment : Fragment() {


    private var _binding: FragmentAddEditPlantBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels()
    private val args: AddEditPlantFragmentArgs by navArgs()

    private var selectedImageUri: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.ivPlantImage.setImageURI(it)
            binding.ivPlantImage.visibility = View.VISIBLE
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val uri = saveBitmapAndGetUri(bitmap)
            if (uri != null) {
                selectedImageUri = uri
                binding.ivPlantImage.setImageURI(uri)
                binding.ivPlantImage.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No image captured", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditPlantBinding.inflate(inflater, container, false)

        val plant = args.plant
        val isEditMode = plant != null
        Log.d("AddEditPlantFragment", "isEditMode: $isEditMode, plant: $plant")

        if (isEditMode) {
            binding.etPlantName.setText(plant!!.commonName)
            binding.etPlantDescription.setText(plant.scientificName)
            binding.etPlantWatering.setText(plant.watering)
            binding.etPlantSunlight.setText(plant.sunlight)

            plant.imageUrl?.let {
                selectedImageUri = it.toUri()
                binding.ivPlantImage.setImageURI(selectedImageUri)
                binding.ivPlantImage.visibility = View.VISIBLE
            }
        }

        binding.btnSelectImage.setOnClickListener {
            val options = arrayOf(
                getString(R.string.choose_from_gallery),
                getString(R.string.take_a_photo)
            )
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select_image_source))
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> galleryLauncher.launch("image/*")
                        1 -> cameraLauncher.launch(null)
                    }
                }
                .show()
        }

        binding.btnSavePlant.setOnClickListener {
            val name = binding.etPlantName.text.toString().trim()
            val description = binding.etPlantDescription.text.toString().trim()
            val watering = binding.etPlantWatering.text.toString().trim()
            val sunlight = binding.etPlantSunlight.text.toString().trim()

            if (name.isBlank()) {
                binding.etPlantName.error = getString(R.string.please_enter_plant_name)
                return@setOnClickListener
            }
            val finalPlant = Plant(
                id = plant?.id ?: 0,
                commonName = name,
                scientificName = description,
                watering = watering,
                sunlight = sunlight,
                imageUrl = selectedImageUri?.toString()
            )

            if (isEditMode) {
                viewModel.updateFavorite(finalPlant)
            } else {
                viewModel.addFavorite(finalPlant)
            }
            Log.d("AddEditPlantFragment", "isEditMode: $isEditMode, plant: $plant")

            Toast.makeText(requireContext(), getString(R.string.plant_saved_successfully), Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        binding.backToListButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun saveBitmapAndGetUri(bitmap: Bitmap?): Uri? {
        if (bitmap == null) return null

        return try {
            val file = File(requireContext().cacheDir, "${System.currentTimeMillis()}.jpg")
            file.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
