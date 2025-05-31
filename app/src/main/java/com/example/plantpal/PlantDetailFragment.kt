package com.example.plantpal

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantpal.databinding.FragmentPlantDetailBinding
import com.example.plantpal.util.Resource
import com.example.plantpal.util.WateringReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlantDetailFragment : Fragment() {

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlantViewModel by viewModels()
    private val args: PlantDetailFragmentArgs by navArgs()
    private lateinit var wateringReminderScheduler: WateringReminderScheduler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wateringReminderScheduler = WateringReminderScheduler(requireContext())

        val plant = args.plant
        val plantId = plant.id
        Log.d("DETAILS_DEBUG", "Sending plantId: $plantId")

        // Fetch detailed information from API
        viewModel.fetchPlantDetails(plantId)

        // Display basic information from passed plant
        binding.tvPlantName.text = plant.commonName ?: "Unknown Plant"
        binding.tvScientificName.text = plant.scientificName

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.plantDetails.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val details = state.data
                        Log.d("DETAILS_DEBUG", "Received details: $details")

                        // Get default care info based on plant type
                        val defaultCareInfo = getDefaultCareInfo(plant.commonName ?: "")

                        // Watering info: prefer API, fallback to default
                        val wateringInfo = details.watering?.takeIf { !it.isNullOrBlank() }
                            ?.let { "\uD83D\uDCA7 Watering: $it" }
                            ?: defaultCareInfo.watering
                        binding.tvWateringInfo.text = wateringInfo

                        // Sunlight info: prefer API, fallback to default
                        val sunlightInfo = details.sunlight?.takeIf { !it.isNullOrEmpty() }
                            ?.joinToString(", ")
                            ?.let { "\u2600\uFE0F Sunlight: $it" }
                            ?: defaultCareInfo.sunlight
                        binding.tvSunlightInfo.text = sunlightInfo

                        // Load and display plant image
                        val imageUrl = details.defaultImage?.mediumUrl ?: plant.imageUrl
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .centerCrop()
                                .into(binding.ivPlantImage)
                        }

                        setupReminderButtons(plant.commonName ?: "Plant")
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Log.e("DETAILS_DEBUG", "Error: ${state.message}")
                        // Show default care info if API fails
                        val defaultCareInfo = getDefaultCareInfo(plant.commonName ?: "")
                        binding.tvWateringInfo.text = defaultCareInfo.watering
                        binding.tvSunlightInfo.text = defaultCareInfo.sunlight
                        Toast.makeText(requireContext(), "No care info from API, showing defaults.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupReminderButtons(plantName: String) {
        binding.btnSetReminder.setOnClickListener {
            viewModel.scheduleWateringReminder(plantName, "average")
            Toast.makeText(requireContext(), "Reminder set for $plantName", Toast.LENGTH_SHORT).show()
        }

        binding.btnCancelReminder.setOnClickListener {
            viewModel.cancelWateringReminder(plantName)
            Toast.makeText(requireContext(), "Reminder cancelled for $plantName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDefaultCareInfo(plantName: String): DefaultCareInfo {
        return when {
            plantName.contains("fir", ignoreCase = true) -> DefaultCareInfo(
                watering = "\uD83D\uDCA7 Water deeply every 7-10 days",
                sunlight = "\u2600\uFE0F Prefers full sun"
            )
            plantName.contains("maple", ignoreCase = true) -> DefaultCareInfo(
                watering = "\uD83D\uDCA7 Water moderately every 5-7 days",
                sunlight = "\u2600\uFE0F Prefers partial to full sun"
            )
            else -> DefaultCareInfo(
                watering = "\uD83D\uDCA7 Water when soil is dry to touch (every 4-7 days)",
                sunlight = "\u2600\uFE0F Moderate, indirect sunlight"
            )
        }
    }

    private fun showImageFullScreen(imageSource: Any) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_fullscreen_image)

        val imageView = dialog.findViewById<ImageView>(R.id.fullscreenImageView)

        Glide.with(requireContext())
            .load(imageSource)
            .into(imageView)

        imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class DefaultCareInfo(
        val watering: String,
        val sunlight: String
    )
}
