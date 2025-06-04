package com.example.plantpal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.plantpal.databinding.ItemReminderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RemindersAdapter(
    private val onCancelReminder: (String) -> Unit
) : ListAdapter<WateringReminder, RemindersAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReminderViewHolder(
        private val binding: ItemReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(reminder: WateringReminder) {
            binding.apply {
                tvPlantName.text = reminder.plantName
                
                val readableFrequency = when (reminder.wateringFrequency) {
                    "frequent" -> itemView.context.getString(R.string.frequency_frequent)
                    "average" -> itemView.context.getString(R.string.frequency_average)
                    "minimum" -> itemView.context.getString(R.string.frequency_minimum)
                    else -> itemView.context.getString(R.string.frequency_unknown)
                }
                tvWateringFrequency.text = readableFrequency
                
                val nextWateringDate = dateFormat.format(Date(reminder.nextWateringDate))
                tvNextWatering.text = "Next watering: $nextWateringDate"

                btnCancelReminder.setOnClickListener {
                    onCancelReminder(reminder.plantName)
                }
            }
        }
    }

    private class ReminderDiffCallback : DiffUtil.ItemCallback<WateringReminder>() {
        override fun areItemsTheSame(oldItem: WateringReminder, newItem: WateringReminder): Boolean {
            return oldItem.plantName == newItem.plantName
        }

        override fun areContentsTheSame(oldItem: WateringReminder, newItem: WateringReminder): Boolean {
            return oldItem == newItem
        }
    }
} 