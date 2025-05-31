package com.example.plantpal

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantpal.databinding.ItemPlantBinding
import com.google.android.material.card.MaterialCardView

class ApiPlantAdapter(
    private val onItemClick: (Plant) -> Unit,
    private val onItemLongClick: (Plant) -> Unit,
    private val onFavoriteClick: (Plant) -> Unit,
    private val isFavoriteCheck: (Int) -> Boolean
) : ListAdapter<Plant, ApiPlantAdapter.PlantViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Plant>() {
            override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = ItemPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(getItem(position))

        (holder.itemView as MaterialCardView).setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    val scaleUp = AnimationUtils.loadAnimation(view.context, R.anim.scale_up)
                    view.startAnimation(scaleUp)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val scaleDown = AnimationUtils.loadAnimation(view.context, R.anim.scale_down)
                    view.startAnimation(scaleDown)
                }
            }
            false
        }

        val context = holder.itemView.context
        val layoutParams = holder.itemView.layoutParams
        val orientation = context.resources.configuration.orientation
        val density = context.resources.displayMetrics.density
        val spacingPx = (8 * density).toInt()

        if (layoutParams is ViewGroup.MarginLayoutParams) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                val maxWidthPx = (300 * density).toInt()
                layoutParams.width = maxWidthPx
                val screenWidth = context.resources.displayMetrics.widthPixels
                val sideMargin = ((screenWidth - maxWidthPx) / 2).coerceAtMost((16 * density).toInt())
                layoutParams.setMargins(sideMargin, spacingPx, sideMargin, spacingPx)
            } else {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.setMargins(spacingPx, spacingPx, spacingPx, spacingPx)
            }
            holder.itemView.layoutParams = layoutParams
        }
    }

    inner class PlantViewHolder(private val binding: ItemPlantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            Log.d("PLANT_DEBUG", "Binding: ${plant.commonName}")

            binding.tvPlantName.text = plant.commonName ?: "Unknown Plant"
            binding.tvWateringInfo.text = plant.scientificName ?: ""
            
            if (!plant.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(plant.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .override(200, 200)
                    .centerCrop()
                    .into(binding.ivPlantImage)
            } else {
                binding.ivPlantImage.setImageResource(R.drawable.ic_launcher_background)
            }

            val isFavorite = isFavoriteCheck(plant.id)
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_launcher_background else R.drawable.ic_launcher_background
            )
            binding.btnFavorite.setColorFilter(
                binding.root.context.getColor(
                    if (isFavorite) R.color.greenPrimary else R.color.black
                )
            )

            binding.btnFavorite.setOnClickListener {
                onFavoriteClick(plant)
                notifyItemChanged(bindingAdapterPosition)
            }

            binding.root.setOnClickListener { onItemClick(plant) }
            binding.root.setOnLongClickListener {
                onItemLongClick(plant)
                true
            }
        }
    }
}
