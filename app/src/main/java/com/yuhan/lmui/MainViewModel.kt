package com.yuhan.lmui

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yuhan.lmui.util.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val pm = application.packageManager
    private val prefsManager = PrefsManager(application)

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps

    private val _favorites = MutableStateFlow<List<AppInfo>>(emptyList())
    val favorites: StateFlow<List<AppInfo>> = _favorites

    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            val allApps = withContext(Dispatchers.IO) {
                val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                pm.queryIntentActivities(intent, 0)
                    .filter { it.activityInfo.packageName != getApplication<Application>().packageName }
                    .map { AppInfo(it.loadLabel(pm).toString(), it.activityInfo.packageName, it.loadIcon(pm)) }
                    .distinctBy { it.packageName }
                    .sortedBy { it.label.lowercase() }
            }

            val hiddenApps = prefsManager.getHiddenApps()
            val favoritePackages = prefsManager.getFavorites()

            _apps.value = allApps.filter { !hiddenApps.contains(it.packageName) }
            _favorites.value = allApps.filter { favoritePackages.contains(it.packageName) }
        }
    }

    fun updateWeather(data: WeatherData) {
        _weatherData.value = data
    }
}
