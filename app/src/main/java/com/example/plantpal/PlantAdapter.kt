package com.example.plantpal

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantpal.databinding.ItemPlantBinding
import com.google.android.material.card.MaterialCardView


class PlantAdapter(
    private val onItemClick: (Plant) -> Unit,
    private val onItemLongClick: (Plant) -> Unit,
    private val onFavoriteClick: (Plant) -> Unit,
    private val isFavoriteCheck: (Int) -> Boolean
) : ListAdapter<Plant, PlantAdapter.PlantViewHolder>(DIFF_CALLBACK) {

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
            binding.tvPlantName.text = plant.commonName ?: "Unknown Plant"
            binding.tvWateringInfo.text = plant.scientificName ?: ""

            Glide.with(binding.root.context)
                .load(plant.imageUrl ?: R.drawable.plantpal_icon)
                .override(200, 200)
                .centerCrop()
                .into(binding.ivPlantImage)

            binding.ivEditHint.visibility = View.VISIBLE
            binding.tvHintEdit.visibility = View.VISIBLE


            val isFavorite = isFavoriteCheck(plant.id)
            binding.btnFavorite.setImageResource(
                if (isFavorite) android.R.drawable.ic_menu_delete else android.R.drawable.ic_menu_add
            )


            binding.btnFavorite.setOnClickListener {
                if (isFavorite) {
                    // אם הצמח כבר מועדף – הצג התראה להסרה
                    AlertDialog.Builder(binding.root.context)
                        .setTitle("Remove from favorites?")
                        .setMessage("Are you sure you want to remove this plant from your list?")
                        .setPositiveButton("Yes") { _, _ ->
                            onFavoriteClick(plant)
                            notifyItemChanged(adapterPosition)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                } else {
                    // אם לא מועדף – פשוט הוסף
                    onFavoriteClick(plant)
                    notifyItemChanged(adapterPosition)
                }
            }


            binding.root.setOnClickListener { onItemClick(plant) }
            binding.root.setOnLongClickListener {
                onItemLongClick(plant)
                true
            }
        }
    }
}
