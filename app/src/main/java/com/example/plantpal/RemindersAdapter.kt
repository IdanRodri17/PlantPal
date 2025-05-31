package com.example.plantpal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.plantpal.databinding.ItemReminderBinding
import com.example.plantpal.model.WateringReminder

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

        fun bind(reminder: WateringReminder) {
            binding.apply {
                tvPlantName.text = reminder.plantName
                tvWateringFrequency.text = when (reminder.wateringFrequency) {
                    "frequent" -> "Every 2-3 days"
                    "average" -> "Every 4-7 days"
                    "minimum" -> "Every 7-14 days"
                    else -> "Every week"
                }
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