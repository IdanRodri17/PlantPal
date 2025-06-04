package com.example.plantpal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantpal.databinding.FragmentFavoritePlantDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritePlantDetailFragment : Fragment() {

    private var _binding: FragmentFavoritePlantDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by viewModels()
    private val args: FavoritePlantDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritePlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.apply {
            // Load plant image
            Glide.with(this@FavoritePlantDetailFragment)
                .load(args.plant.imageUrl ?: R.drawable.plantpal_icon_small)
                .into(ivPlantImage)

            // Set plant details
            tvPlantName.text = args.plant.commonName ?: "Unknown"
            tvScientificName.text = args.plant.scientificName ?: ""

            // Set default radio button
            rgWateringFrequency.check(R.id.rbAverage)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            btnAddReminder.setOnClickListener {
                val frequency = when (rgWateringFrequency.checkedRadioButtonId) {
                    R.id.rbFrequent -> "frequent"
                    R.id.rbAverage -> "average"
                    R.id.rbMinimum -> "minimum"
                    else -> "average"
                }

                val plantName = args.plant.commonName ?: "Unnamed Plant"
                viewModel.scheduleWateringReminder(plantName, frequency)
                Toast.makeText(
                    requireContext(),
                    "Reminder set for $plantName",
                    Toast.LENGTH_SHORT
                ).show()
            }

            btnTestReminder.setOnClickListener {
                val plantName = args.plant.commonName ?: "Unnamed Plant"
                viewModel.scheduleTestReminder(plantName)
                Toast.makeText(
                    requireContext(),
                    "Test reminder set for $plantName (10 seconds)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 