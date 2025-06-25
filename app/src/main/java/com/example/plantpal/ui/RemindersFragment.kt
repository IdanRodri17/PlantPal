package com.example.plantpal.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantpal.PlantViewModel
import com.example.plantpal.RemindersAdapter
import com.example.plantpal.WateringReminder
import com.example.plantpal.databinding.FragmentRemindersBinding
import com.example.plantpal.util.WateringReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RemindersFragment : Fragment() {

    private var _binding: FragmentRemindersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by viewModels()
    private lateinit var remindersAdapter: RemindersAdapter
    private val TAG = "RemindersFragment"

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
            Log.d(TAG, "Received ${workInfos.size} workInfos")

            val reminders = workInfos.mapNotNull { workInfo ->
                val frequency = workInfo.tags.firstOrNull {
                    it == "frequent" || it == "average" || it == "minimum"
                } ?: "average"

                val plantName = workInfo.tags.firstOrNull {
                    it != "frequent" && it != "average" && it != "minimum" && it != "watering_reminder"
                } ?: return@mapNotNull null

                val nextWateringDate = System.currentTimeMillis() + when (frequency) {
                    "frequent" -> TimeUnit.DAYS.toMillis(WateringReminderScheduler.FREQUENT_DAYS)
                    "average" -> TimeUnit.DAYS.toMillis(WateringReminderScheduler.AVERAGE_DAYS)
                    "minimum" -> TimeUnit.DAYS.toMillis(WateringReminderScheduler.MINIMUM_DAYS)
                    else -> TimeUnit.DAYS.toMillis(WateringReminderScheduler.DEFAULT_DAYS)
                }

                Log.d(TAG, "Parsed reminder: Plant=$plantName, Frequency=$frequency, Next=$nextWateringDate")

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