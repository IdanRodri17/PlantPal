package com.example.plantpal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import com.example.plantpal.databinding.FragmentActiveRemindersBinding
import com.example.plantpal.util.WateringReminderScheduler
import com.example.plantpal.workers.WateringReminderWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ActiveRemindersFragment : Fragment() {

    private var _binding: FragmentActiveRemindersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlantViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()

    private lateinit var remindersAdapter: RemindersAdapter
    private val TAG = "ActiveRemindersFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveRemindersBinding.inflate(inflater, container, false)
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
        favoriteViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            val reminderList = mutableListOf<WateringReminder>()

            favorites.forEach { plant ->
                val plantName = plant.commonName ?: return@forEach

                viewModel.getReminderForPlant(plantName).observe(viewLifecycleOwner) { workInfos ->
                    val activeInfo = workInfos.firstOrNull { it.state == WorkInfo.State.ENQUEUED }

                    if (activeInfo != null) {
                        val outputPlantName = activeInfo.outputData.getString(WateringReminderWorker.PLANT_NAME_KEY) ?: plantName
                        val frequency = activeInfo.tags.firstOrNull {
                            it == "frequent" || it == "average" || it == "minimum"
                        } ?: "average"

                        val nextWateringDate = System.currentTimeMillis() + when (frequency) {
                            "frequent" -> TimeUnit.DAYS.toMillis(WateringReminderScheduler.FREQUENT_DAYS)
                            "average" -> TimeUnit.DAYS.toMillis(WateringReminderScheduler.AVERAGE_DAYS)
                            "minimum" -> TimeUnit.DAYS.toMillis(WateringReminderScheduler.MINIMUM_DAYS)
                            else -> TimeUnit.DAYS.toMillis(WateringReminderScheduler.DEFAULT_DAYS)
                        }

                        val reminder = WateringReminder(
                            plantName = outputPlantName,
                            wateringFrequency = frequency,
                            nextWateringDate = nextWateringDate
                        )

                        reminderList.add(reminder)
                        remindersAdapter.submitList(reminderList.toList())
                        binding.tvNoReminders.visibility = View.GONE
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
