package com.example.plantpal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.plantpal.databinding.ItemReminderBinding
<<<<<<< HEAD
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
=======
import com.example.plantpal.model.WateringReminder
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8

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

<<<<<<< HEAD
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
                    itemView.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction {
                            onCancelReminder(reminder.plantName)
                        }
                        .start()
=======
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
>>>>>>> 70b5208f6b4e5f358a23068484823b97a630b2a8
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