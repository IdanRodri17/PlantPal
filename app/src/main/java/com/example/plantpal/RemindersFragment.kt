package com.example.plantpal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.WorkInfo
import com.example.plantpal.databinding.FragmentRemindersBinding
import com.example.plantpal.model.WateringReminder
import com.example.plantpal.workers.WateringReminderWorker
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
            Log.d("REMINDER_DEBUG", "RemindersFragment received ${workInfos.size} workInfos")
            val reminders = workInfos.mapNotNull { workInfo ->
                val inputData = workInfo.inputData
                val plantName = inputData.getString(WateringReminderWorker.PLANT_NAME_KEY)
                if (plantName == null) {
                    return@mapNotNull null
                }

                val frequency = workInfo.tags.firstOrNull { it == "frequent" || it == "average" || it == "minimum" } ?: "average"
                val nextWateringDate = System.currentTimeMillis() + when (frequency) {
                    "frequent" -> TimeUnit.DAYS.toMillis(2) + workInfo.initialDelayMillis
                    "average" -> TimeUnit.DAYS.toMillis(5) + workInfo.initialDelayMillis
                    "minimum" -> TimeUnit.DAYS.toMillis(10) + workInfo.initialDelayMillis
                    else -> TimeUnit.DAYS.toMillis(7) + workInfo.initialDelayMillis
                }
                Log.d("REMINDER_DEBUG", "Parsed reminder: Plant=$plantName, Frequency=$frequency, Next Watering=$nextWateringDate")
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