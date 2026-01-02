package com.yuhan.lmui.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("launcher_prefs", Context.MODE_PRIVATE)

    fun saveFavorites(favorites: Set<String>) {
        prefs.edit { putStringSet("favorites", favorites) }
    }

    fun getFavorites(): Set<String> {
        return prefs.getStringSet("favorites", emptySet()) ?: emptySet()
    }

    fun saveWallpaper(uri: String) {
        prefs.edit { putString("wallpaper", uri) }
    }

    fun getWallpaper(): String? {
        return prefs.getString("wallpaper", null)
    }

    fun setFullScreen(enabled: Boolean) {
        prefs.edit { putBoolean("full_screen", enabled) }
    }

    fun isFullScreen(): Boolean {
        return prefs.getBoolean("full_screen", false)
    }

    fun setAppDrawerEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("app_drawer_enabled", enabled) }
    }

    fun isAppDrawerEnabled(): Boolean {
        return prefs.getBoolean("app_drawer_enabled", true)
    }

    fun setSearchBarEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("search_bar_enabled", enabled) }
    }

    fun isSearchBarEnabled(): Boolean {
        return prefs.getBoolean("search_bar_enabled", true)
    }

    /**
     * Set drawer opacity.
     * 75% opacity is approximately 191 in 0-255 range.
     */
    fun setDrawerOpacity(opacity: Int) {
        prefs.edit { putInt("drawer_opacity", opacity) }
    }

    fun getDrawerOpacity(): Int {
        // Default changed to ~75% (191) as requested.
        return prefs.getInt("drawer_opacity", 191)
    }

    fun setBlurMagnitude(magnitude: Int) {
        prefs.edit { putInt("blur_magnitude", magnitude) }
    }

    fun getBlurMagnitude(): Int {
        return prefs.getInt("blur_magnitude", 50)
    }

    fun setIconAnimationEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("icon_animation_enabled", enabled) }
    }

    fun isIconAnimationEnabled(): Boolean {
        return prefs.getBoolean("icon_animation_enabled", true)
    }

    fun setSwipeDownNotificationsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("swipe_down_notifications", enabled) }
    }

    fun isSwipeDownNotificationsEnabled(): Boolean {
        return prefs.getBoolean("swipe_down_notifications", true)
    }

    fun setVerticalListModeEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("vertical_list_mode", enabled) }
    }

    fun isVerticalListModeEnabled(): Boolean {
        return prefs.getBoolean("vertical_list_mode", false)
    }

    fun setAppFont(fontFamily: String) {
        prefs.edit { putString("app_font", fontFamily) }
    }

    fun getAppFont(): String {
        return prefs.getString("app_font", "inter_regular") ?: "inter_regular"
    }

    fun saveHiddenApps(hiddenApps: Set<String>) {
        prefs.edit { putStringSet("hidden_apps", hiddenApps) }
    }

    fun getHiddenApps(): Set<String> {
        return prefs.getStringSet("hidden_apps", emptySet()) ?: emptySet()
    }

    fun unhideApp(packageName: String) {
        val hidden = getHiddenApps().toMutableSet()
        if (hidden.remove(packageName)) {
            saveHiddenApps(hidden)
        }
    }
}