package com.example.plantpal.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantpal.ApiPlant
import com.example.plantpal.FavoritePlant
import com.example.plantpal.FavoriteViewModel
import com.example.plantpal.Plant
import com.example.plantpal.PlantViewModel
import com.example.plantpal.R
import com.example.plantpal.databinding.FragmentPlantDetailBinding
import com.example.plantpal.toPlant
import com.example.plantpal.util.Resource
import com.google.gson.Gson
import kotlin.collections.joinToString

import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class PlantDetailFragment : Fragment() {

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val viewModel: PlantViewModel by viewModels()
    private val args: PlantDetailFragmentArgs by navArgs()

    private var plantId: Int = -1
    private var currentImageUrl: String? = null
    private var wateringRaw: String? = null
    private var sunlightRaw: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plant = args.plant
        plantId = plant.id
        Log.d("DETAILS_DEBUG", "Sending plantId: $plantId")

        binding.tvPlantName.text = plant.commonName ?: "Unknown Plant"
        binding.tvScientificName.text = plant.scientificName?.joinToString(", ") ?: "N/A"
        binding.tvWateringInfo.text = "Watering - ${plant.watering ?: "Loading . ."}"
        binding.tvSunlightInfo.text = "Sunlight - ${plant.sunlight ?: "Loading . ."}"

        // הצגת תמונה
        val imageUrl = plant.defaultImage?.originalUrl

        val imageToLoad = if (!imageUrl.isNullOrBlank()) {
            imageUrl
        } else {
            R.drawable.plantpal_icon
        }

        Glide.with(requireContext())
            .load(imageToLoad)
            .centerCrop()
            .into(binding.ivPlantImage)


        binding.ivPlantImage.setOnClickListener {
            showImageFullScreen(imageUrl ?: R.drawable.plantpal_icon)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        

        // שליפת מידע נוסף מה-API
        viewModel.fetchPlantDetails(plantId)
        viewModel.plantDetails.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val details = state.data
                    Log.d("DEBUG_WATERING", "watering: ${details.watering}")
                    Log.d("DETAILS_JSON", Gson().toJson(details))

                    // - לא קיים עדכון טקסטים עם המידע החדש
                    //binding.tvWateringInfo.text = "Watering: ${details.watering ?: "N/A"}"
                    //🔍 The 'watering' field often returns null from the API.
                    // To demonstrate proper UI handling and fallback behavior,
                    // I simulate a value from the documented options instead of leaving it empty.
                    // בדיקה: אם אין מידע מה-API, נבחר ערך רנדומלי
                    //val wateringRaw = details.watering ?: wateringOptions.random()

                    val wateringOptions = listOf("frequent", "average", "minimum", "none")
                    wateringRaw = details.watering
                    val wateringText = when (wateringRaw?.lowercase()) {
                        "frequent" -> "💧💧💧 frequent"
                        "average" -> "💧💧 average"
                        "minimum" -> "💧 minimum "
                        "none" -> "🚫 No water needed"
                        else -> "❓ unavailable "
                    }

                    binding.tvWateringInfo.text = wateringText

                    // מגיע NULL ולכן נבצע RANDOM
                    //binding.tvSunlightInfo.text = "Sunlight: ${details.sunlight?.joinToString(", ") ?: "N/A"}"
                    // בדיקה: אם אין מידע מה-API, נבחר ערך רנדומלי
                    // 🔍 The 'sunlight' field is sometimes null or missing from the API.
// To               maintain a consistent user experience,
// I                randomly display one of the valid values based on the API documentation.

                    val sunlightOptions = listOf("full_sun", "part_shade", "full_shade", "sun-part_shade")



                    val sunlightTexts = details.sunlight?.map { sunlightValue ->
                        when (sunlightValue.lowercase()) {
                            "full sun" -> "☀️ full sun"
                            "part shade" -> "🌤 part shade"
                            "sun-part shade" -> "⛅ sun part shade"
                            "filtered shade" -> "🌫 filtered shade"
                            "full shade" -> "🌑 full shade"
                            else -> "❓ $sunlightValue"
                        }
                    } ?: listOf("❓ unknown")

                    sunlightRaw = details.sunlight?.joinToString ( " | " )
                    binding.tvSunlightInfo.text = sunlightTexts.joinToString(" | ")
                    Log.d("DETAILS_DEBUG", "Sunlight raw from API: ${details.sunlight}")
                    //Log.d("DETAILS_DEBUG", "sunlightRaw = $sunlightRaw")



                    // טעינת תמונה מעודכנת אם קיימת
                    val updatedImage = details.defaultImage?.original_url
                    if (!updatedImage.isNullOrBlank()) {
                        currentImageUrl = updatedImage
                        Glide.with(requireContext())
                            .load(updatedImage)
                            .centerCrop()
                            .into(binding.ivPlantImage)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Failed to load details", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.btnSaveFullPlant.setOnClickListener {
            favoriteViewModel.addFavorite(
                Plant(
                    id = plantId,
                    commonName = binding.tvPlantName.text.toString(),
                    scientificName = binding.tvScientificName.text.toString(),
                    imageUrl = currentImageUrl,
                    watering = binding.tvWateringInfo.text.toString(),
                    sunlight = binding.tvSunlightInfo.text.toString()
                )
            )
            Toast.makeText(requireContext(), "Save Successfully", Toast.LENGTH_SHORT).show()
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
}
