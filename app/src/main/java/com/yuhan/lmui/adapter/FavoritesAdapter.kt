package com.yuhan.lmui.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuhan.lmui.databinding.ItemFavoriteBinding
import com.yuhan.lmui.AppInfo
import com.yuhan.lmui.R
import com.yuhan.lmui.util.PrefsManager

class FavoritesAdapter(
    private val onAppClick: (AppInfo, Boolean, View) -> Unit
) : ListAdapter<AppInfo, FavoritesAdapter.FavoriteViewHolder>(AppDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val prefsManager = PrefsManager(binding.root.context)

        fun bind(appInfo: AppInfo) {
            binding.appIcon.setImageDrawable(appInfo.icon)
            binding.appName.text = appInfo.label

            // Apply custom font consistent with AppsAdapter
            val fontResId = when(prefsManager.getAppFont()) {
                "inter_regular" -> R.font.inter_regular
                "inter_medium" -> R.font.inter_medium
                "inter_light" -> R.font.inter_light
                "outfit_light" -> R.font.outfit_light
                "outfit_semibold" -> R.font.outfit_semibold
                "playfair_display_italic" -> R.font.playfair_display_italic
                else -> R.font.inter_regular
            }
            binding.appName.typeface = ResourcesCompat.getFont(binding.root.context, fontResId)

            // Single click to launch
            binding.root.setOnClickListener {
                onAppClick(appInfo, false, binding.root)
            }

            // Long click to remove from favorites
            binding.root.setOnLongClickListener {
                onAppClick(appInfo, true, binding.root)
                true
            }

            // Touch animation consistent with AppsAdapter
            if (prefsManager.isIconAnimationEnabled()) {
                binding.root.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).start()
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                        }
                    }
                    false
                }
            } else {
                binding.root.setOnTouchListener(null)
            }
        }
    }

    class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }
}
