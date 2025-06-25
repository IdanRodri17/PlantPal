package com.example.plantpal

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.plantpal.databinding.ItemPlantBinding
import com.google.android.material.card.MaterialCardView

class ApiPlantAdapter(
    private val onItemClick: (ApiPlant) -> Unit,
    private val onItemLongClick: (ApiPlant) -> Unit,
    private val onFavoriteClick: (ApiPlant) -> Unit,
    private val isFavoriteCheck: (Int) -> Boolean
) : androidx.recyclerview.widget.ListAdapter<ApiPlant, ApiPlantAdapter.PlantViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ApiPlant>() {
            override fun areItemsTheSame(oldItem: ApiPlant, newItem: ApiPlant): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ApiPlant, newItem: ApiPlant): Boolean {
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

        // התאמות רוחב ומרווחים
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

        fun bind(plant: ApiPlant) {
            Log.d("PLANT_DEBUG", "Binding plant: ${plant.commonName}")

            binding.tvPlantName.text = plant.commonName ?: "Unknown Plant"


            binding.tvWateringInfo.text = plant.watering
            binding.tvSunlightInfo.text = plant.sunlight.toString()


            // Watering info
            val wateringEmoji = when (plant.watering?.lowercase()) {
                "frequent" -> "💧💧💧"
                "average" -> "💧💧"
                "minimum" -> "💧"
                "none" -> "🚫"
                else -> ""
            }
            binding.tvWateringInfo.text = "Watering and Sunlight $wateringEmoji ${plant.watering ?:""}"

            // Sunlight info
            val sunlightEmoji = when (plant.sunlight?.firstOrNull()?.lowercase()) {
                "full_sun" -> "☀️"
                "sun-part_shade" -> "⛅"
                "part_shade" -> "🌤️"
                "full_shade" -> "🌑"
                else -> "❓"
            }
            binding.tvSunlightInfo.text = "☀️  💧   \nClick for info "

            binding.ivEditHint.visibility = View.GONE
            binding.tvHintEdit.visibility = View.GONE



            val imageUrl = plant.defaultImage?.mediumUrl
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                    .signature(ObjectKey(imageUrl)) // חתימה זהה לזו שב־preload
                    .placeholder(R.drawable.plantpal_icon_small)
                    .override(200, 200)
                    .centerCrop()
                    .into(binding.ivPlantImage)
            } else {
                binding.ivPlantImage.setImageResource(R.drawable.plantpal_icon_small)
            }
            Log.d("IMAGE_URL_DEBUG", "Plant: ${plant.commonName}, URL: ${plant.defaultImage?.mediumUrl}")

            val isFavorite = isFavoriteCheck(plant.id)
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.plantpal_icon else R.drawable.add_plus
            )





            binding.btnFavorite.setOnClickListener {
                Log.d("FAV_DEBUG", "Favorite button clicked for plant id: ${plant.id}")
                onFavoriteClick(plant)
                notifyItemChanged(adapterPosition)
            }

            binding.root.setOnClickListener { onItemClick(plant) }
            binding.root.setOnLongClickListener {
                onItemLongClick(plant)
                true
            }
        }
    }
}
