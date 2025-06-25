package com.example.plantpal.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantpal.FavoritePlant
import com.example.plantpal.FavoriteViewModel
import com.example.plantpal.Plant
import com.example.plantpal.PlantAdapter
import com.example.plantpal.R
import com.example.plantpal.databinding.FragmentPlantListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlantListFragment : Fragment() {

    private var _binding: FragmentPlantListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: PlantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavorites()

        binding.addButton.setOnClickListener {
            val action = PlantListFragmentDirections
                .actionPlantListFragmentToAddEditPlantFragment(null)
            findNavController().navigate(action)
        }

        binding.btnViewReminders.setOnClickListener {
            val action = PlantListFragmentDirections
                .actionPlantListFragmentToActiveRemindersFragment()
            findNavController().navigate(action)
        }

        binding.ivAppLogo.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = PlantAdapter(
            onItemClick = { plant ->
                val action = PlantListFragmentDirections
                    .actionPlantListFragmentToFavoritePlantDetailFragment(
                        FavoritePlant(
                            id = plant.id,
                            commonName = plant.commonName,
                            scientificName = plant.scientificName,
                            imageUrl = plant.imageUrl,
                            watering = plant.watering,
                            sunlight = plant.sunlight
                        )
                    )
                findNavController().navigate(action)
            },
            onItemLongClick = { plant ->
                val action = PlantListFragmentDirections
                    .actionPlantListFragmentToAddEditPlantFragment(plant)
                findNavController().navigate(action)
            },
            onFavoriteClick = { plant ->
                viewModel.toggleFavorite(plant)
            },
            isFavoriteCheck = { id ->
                viewModel.isFavorite(id)
            }
        )
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter
    }

    private fun observeFavorites() {
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            adapter.submitList(favorites.map {
                Plant(
                    id = it.id,
                    commonName = it.commonName,
                    scientificName = it.scientificName,
                    watering = it.watering,
                    sunlight = it.sunlight,
                    imageUrl = it.imageUrl
                )
            })
        }
    }

    private fun showDeleteDialog(plant: Plant) {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_plant))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.toggleFavorite(plant)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
