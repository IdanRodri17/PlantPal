package com.example.plantpal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantpal.databinding.FragmentRemindersBinding
import com.example.plantpal.model.WateringReminder
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RemindersFragment : Fragment() {

    private var _binding: FragmentRemindersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by viewModels()
    private lateinit var remindersAdapter: RemindersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeReminders()
    }

    private fun setupRecyclerView() {
        remindersAdapter = RemindersAdapter(
            onCancelReminder = { plantName ->
                viewModel.cancelWateringReminder(plantName)
            }
        )
        binding.rvReminders.apply {
            adapter = remindersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeReminders() {
        viewModel.activeReminders.observe(viewLifecycleOwner) { workInfos ->
            val reminders = workInfos.mapNotNull { workInfo ->
                val plantName = workInfo.tags.firstOrNull { it != "watering_reminder" } ?: return@mapNotNull null
                val frequency = workInfo.tags.firstOrNull { it != "watering_reminder" && it != plantName } ?: "average"
                val nextWateringDate = System.currentTimeMillis() + when (frequency) {
                    "frequent" -> TimeUnit.DAYS.toMillis(2)
                    "average" -> TimeUnit.DAYS.toMillis(5)
                    "minimum" -> TimeUnit.DAYS.toMillis(10)
                    else -> TimeUnit.DAYS.toMillis(7)
                }
                WateringReminder(
                    plantName = plantName,
                    wateringFrequency = frequency,
                    nextWateringDate = nextWateringDate
                )
            }
            remindersAdapter.submitList(reminders)
            binding.tvNoReminders.visibility = if (reminders.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 