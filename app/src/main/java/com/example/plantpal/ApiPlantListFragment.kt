package com.example.plantpal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantpal.databinding.FragmentApiProductListBinding
import com.example.plantpal.model.ApiPlantResponse
import com.example.plantpal.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ApiPlantListFragment : Fragment() {

    private var _binding: FragmentApiProductListBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by viewModels()
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
        plantViewModel.fetchPlants()

        setupRecyclerView()
        observePlants()

        binding.btnGoToFavorites.setOnClickListener {
            findNavController().navigate(R.id.action_apiPlantListFragment_to_plantListFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = ApiPlantAdapter(
            onItemClick = { plant ->
                val action = ApiPlantListFragmentDirections
                    .actionApiPlantListFragmentToPlantDetailFragment(plant)
                findNavController().navigate(action)
            },
            onItemLongClick = { /* אפשרות עתידית */ },
            onFavoriteClick = { plant ->
                favoriteViewModel.toggleFavorite(plant)
            },
            isFavoriteCheck = { id ->
                favoriteViewModel.isFavorite(id)
            }
        )

        binding.rvApiProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvApiProducts.adapter = adapter
    }

    private fun observePlants() {
        viewLifecycleOwner.lifecycleScope.launch {
            plantViewModel.plants.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        Log.d("ApiPlantListFragment", "Loading plants...")
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Log.d("ApiPlantListFragment", "Success! Data: ${state.data}")
                        binding.progressBar.visibility = View.GONE
                        val response = state.data as ApiPlantResponse
                        Log.d("ApiPlantListFragment", "Number of plants: ${response.data.size}")
                        val plants = response.data.map { apiPlant ->
                            Log.d("ApiPlantListFragment", "Converting plant: ${apiPlant.commonName}")
                            Plant(
                                id = apiPlant.id,
                                commonName = apiPlant.commonName,
                                scientificName = apiPlant.scientificName?.joinToString(", "),
                                watering = apiPlant.watering,
                                sunlight = apiPlant.sunlight?.joinToString(", "),
                                imageUrl = apiPlant.imageUrl
                            )
                        }
                        Log.d("ApiPlantListFragment", "Submitting ${plants.size} plants to adapter")
                        adapter.submitList(plants)
                    }
                    is Resource.Error -> {
                        Log.e("ApiPlantListFragment", "Error: ${state.message}")
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
