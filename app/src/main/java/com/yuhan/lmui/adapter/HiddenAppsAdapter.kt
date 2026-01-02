package com.yuhan.lmui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yuhan.lmui.databinding.ItemHiddenAppBinding

data class HiddenApp(val packageName: String, val label: String, val icon: android.graphics.drawable.Drawable)

class HiddenAppsAdapter(
    private var apps: List<HiddenApp>,
    private val onUnhideClick: (String) -> Unit
) : RecyclerView.Adapter<HiddenAppsAdapter.ViewHolder>() {

    fun updateData(newApps: List<HiddenApp>) {
        apps = newApps
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHiddenAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.bind(app)
    }

    override fun getItemCount() = apps.size

    inner class ViewHolder(private val binding: ItemHiddenAppBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(app: HiddenApp) {
            binding.appName.text = app.label
            binding.appIcon.setImageDrawable(app.icon)
            binding.unhideButton.setOnClickListener { onUnhideClick(app.packageName) }
        }
    }
}