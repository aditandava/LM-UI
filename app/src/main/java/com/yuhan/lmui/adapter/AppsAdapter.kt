package com.yuhan.lmui.adapter

import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuhan.lmui.R
import com.yuhan.lmui.AppInfo
import com.yuhan.lmui.util.PrefsManager

class AppsAdapter(private val onItemClick: (AppInfo, Boolean, View) -> Unit) :
    ListAdapter<AppsAdapter.DisplayItem, RecyclerView.ViewHolder>(AppDiffCallback()) {

    private var isVerticalMode = false
    private var currentRawApps: List<AppInfo> = emptyList()

    sealed class DisplayItem {
        data class App(val appInfo: AppInfo) : DisplayItem()
        data class Header(val letter: String) : DisplayItem()
    }

    fun setVerticalMode(enabled: Boolean) {
        if (isVerticalMode != enabled) {
            isVerticalMode = enabled
            refreshList()
        }
    }

    fun isVerticalMode() = isVerticalMode

    fun getSpanSize(position: Int): Int {
        val item = getItem(position)
        return if (isVerticalMode || item is DisplayItem.Header) 4 else 1
    }

    fun submitAppList(apps: List<AppInfo>) {
        currentRawApps = apps
        refreshList()
    }

    private fun refreshList() {
        if (!isVerticalMode) {
            submitList(currentRawApps.map { DisplayItem.App(it) })
        } else {
            val items = mutableListOf<DisplayItem>()
            var currentLetter = ""
            currentRawApps.sortedBy { it.label.uppercase() }.forEach { app ->
                val firstChar = app.label.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
                if (firstChar != currentLetter) {
                    currentLetter = firstChar
                    items.add(DisplayItem.Header(currentLetter))
                }
                items.add(DisplayItem.App(app))
            }
            submitList(items)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DisplayItem.Header -> VIEW_TYPE_HEADER
            is DisplayItem.App -> VIEW_TYPE_APP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drawer_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_drawer, parent, false)
                AppViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is AppViewHolder && item is DisplayItem.App) {
            holder.bind(item.appInfo)
        } else if (holder is HeaderViewHolder && item is DisplayItem.Header) {
            holder.bind(item.letter)
        }
    }

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appName: TextView = itemView.findViewById(R.id.appName)
        private val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        private val iconCard: View = itemView.findViewById(R.id.iconCard)
        private val prefsManager = PrefsManager(itemView.context)

        fun bind(appInfo: AppInfo) {
            appName.text = appInfo.label
            appIcon.setImageDrawable(appInfo.icon)

            val fontResId = when(prefsManager.getAppFont()) {
                "inter_regular" -> R.font.inter_regular
                "inter_medium" -> R.font.inter_medium
                "inter_light" -> R.font.inter_light
                "outfit_light" -> R.font.outfit_light
                "outfit_semibold" -> R.font.outfit_semibold
                "playfair_display_italic" -> R.font.playfair_display_italic
                else -> R.font.inter_regular
            }
            appName.typeface = ResourcesCompat.getFont(itemView.context, fontResId)

            val params = appName.layoutParams as ConstraintLayout.LayoutParams
            
            if (isVerticalMode) {
                // List Mode
                iconCard.visibility = View.GONE
                appName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                appName.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                appName.alpha = 0.7f
                
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                params.topToBottom = -1
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                params.topMargin = 0
                params.marginStart = (24 * itemView.resources.displayMetrics.density).toInt()
                
                itemView.setPadding(0, (12 * itemView.resources.displayMetrics.density).toInt(), 0, (12 * itemView.resources.displayMetrics.density).toInt())
            } else {
                // Grid Mode
                iconCard.visibility = View.VISIBLE
                appName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                appName.gravity = Gravity.CENTER
                appName.alpha = 1.0f
                
                params.topToBottom = R.id.iconCard
                params.topToTop = -1
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                params.topMargin = (8 * itemView.resources.displayMetrics.density).toInt()
                params.marginStart = 0
                
                itemView.setPadding(0, (8 * itemView.resources.displayMetrics.density).toInt(), 0, (8 * itemView.resources.displayMetrics.density).toInt())
            }
            appName.layoutParams = params

            itemView.setOnClickListener { onItemClick(appInfo, false, itemView) }
            itemView.setOnLongClickListener {
                onItemClick(appInfo, true, itemView)
                true
            }

            if (!isVerticalMode && prefsManager.isIconAnimationEnabled()) {
                itemView.setOnTouchListener { v, event ->
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
                itemView.setOnTouchListener(null)
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerText: TextView = itemView.findViewById(R.id.headerText)
        fun bind(letter: String) {
            headerText.text = letter
        }
    }

    class AppDiffCallback : DiffUtil.ItemCallback<DisplayItem>() {
        override fun areItemsTheSame(oldItem: DisplayItem, newItem: DisplayItem): Boolean {
            return if (oldItem is DisplayItem.App && newItem is DisplayItem.App) {
                oldItem.appInfo.packageName == newItem.appInfo.packageName
            } else if (oldItem is DisplayItem.Header && newItem is DisplayItem.Header) {
                oldItem.letter == newItem.letter
            } else false
        }
        override fun areContentsTheSame(oldItem: DisplayItem, newItem: DisplayItem): Boolean = oldItem == newItem
    }

    companion object {
        private const val VIEW_TYPE_APP = 0
        private const val VIEW_TYPE_HEADER = 1
    }
}