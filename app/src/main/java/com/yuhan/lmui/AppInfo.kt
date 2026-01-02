package com.yuhan.lmui

import android.graphics.drawable.Drawable

/**
 * Data model for installed applications.
 */
data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable
)