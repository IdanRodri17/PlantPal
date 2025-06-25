package com.example.plantpal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.example.plantpal.databinding.FragmentApiProductListBinding
import com.example.plantpal.util.Resource
import com.example.plantpal.util.toApiPlant


import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ApiPlantListFragment : Fragment() {

    private var _binding: FragmentApiProductListBinding? = null
    private val binding get() = _binding!!


    private val favoriteViewModel: FavoriteViewModel by viewModels ()
    private val plantViewModel: PlantViewModel by viewModels()

    private lateinit var adapter: ApiPlantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ApiPlantListFragment", "Fragment loaded")

        binding.indoorChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_all -> plantViewModel.fetchPlants(indoor = null)
                R.id.chip_indoor -> plantViewModel.fetchPlants(indoor = 1)
                R.id.chip_outdoor -> plantViewModel.fetchPlants(indoor = 0)
            }
        }

        setupRecyclerView()
        observePlants()

        // החזרת מצב הסינון האחרון (ה-chip הנבחר)
        val lastCheckedId = when (plantViewModel.selectedFilter) {
            null -> R.id.chip_all
            1 -> R.id.chip_indoor
            0 -> R.id.chip_outdoor
            else -> R.id.chip_all
        }
        binding.indoorChipGroup.check(lastCheckedId)

        // הפעלת סינון לפי המצב האחרון
        plantViewModel.fetchPlants(plantViewModel.selectedFilter)

        binding.btnGoToFavorites.setOnClickListener {
            findNavController().navigate(R.id.action_apiPlantListFragment_to_plantListFragment)
        }
    }


    private fun setupRecyclerView() {
        adapter = ApiPlantAdapter(
            onItemClick = { apiPlant ->
                val action = ApiPlantListFragmentDirections
                    .actionApiPlantListFragmentToPlantDetailFragment(apiPlant)
                findNavController().navigate(action)
            },
            onItemLongClick = { /* אפשרות עתידית */ },
            onFavoriteClick = { apiPlant ->
                Log.d("FAV_DEBUG", "onFavoriteClick called for id: ${apiPlant.id}")
                favoriteViewModel.toggleFavorite(apiPlant.toPlant())
            },
            isFavoriteCheck = { id ->
                favoriteViewModel.isFavorite(id)
            }
        )

        binding.rvApiProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvApiProducts.adapter = adapter
    }




    private fun observePlants() {

        plantViewModel.plantList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val plants = state.data
                    adapter.submitList(plants)
                    plants.forEach { plant ->
                        val imageUrl = plant.defaultImage?.mediumUrl
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL) // גם לדיסק וגם לזיכרון
                                .signature(ObjectKey(imageUrl)) // מוסיף חתימה קבועה
                                .preload()
                        }
                    }

                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(),
                        getString(R.string.internet_connection_error, state.message), Toast.LENGTH_SHORT).show()

                    // Fallback: טען ממטמון אם יש שגיאה
                    plantViewModel.fetchPlantsCacheFirst()
                }
            }
        }

            // מאזין גם לרשימת צמחים מהמטמון
            plantViewModel.cachedPlants.observe(viewLifecycleOwner) { cachedState ->
                when (cachedState) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val cachedList = cachedState.data.map { it.toApiPlant() }
                        adapter.submitList(cachedList)
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), cachedState.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
