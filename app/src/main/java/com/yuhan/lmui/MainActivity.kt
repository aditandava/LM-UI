package com.yuhan.lmui

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.transition.Fade
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yuhan.lmui.adapter.AppsAdapter
import com.yuhan.lmui.adapter.FavoritesAdapter
import com.yuhan.lmui.databinding.ActivityMainBinding
import com.yuhan.lmui.util.PrefsManager
import com.yuhan.lmui.util.ExplodeItemAnimator
import kotlinx.coroutines.*
import java.io.File
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var prefsManager: PrefsManager
    private lateinit var weatherManager: WeatherManager
    private lateinit var locationManager: LocationManager
    private var chargingAnimator: ObjectAnimator? = null
    
    private val viewModel: MainViewModel by viewModels()

    private val appsAdapter = AppsAdapter { appInfo: AppInfo, isLongClick: Boolean, view: View ->
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            if (isLongClick) showAppOptions(appInfo, view) else launchApp(appInfo, view)
        }
    }

    private val favoritesAdapter = FavoritesAdapter { appInfo: AppInfo, isLongClick: Boolean, view: View ->
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            if (isLongClick) showAppOptions(appInfo, view) else launchApp(appInfo, view)
        }
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateBattery(intent)
        }
    }

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.loadApps()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefsManager = PrefsManager(this)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherManager = WeatherManager(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        setupInsets()
        setupBottomSheet()
        setupUI()
        setupGestures()
        setupRecyclerViews()
        setupBackNavigation()
        setupObservers()

        updateDateTime()
        startClockUpdate()
        startWeatherUpdate()
        loadLauncherWallpaper()
        
        checkLocationPermissions()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apps.collect { apps ->
                        appsAdapter.submitAppList(apps)
                    }
                }
                launch {
                    viewModel.favorites.collect { favorites ->
                        favoritesAdapter.submitList(favorites)
                    }
                }
                launch {
                    viewModel.weatherData.collect { data ->
                        data?.let { updateWeatherUI(it) }
                    }
                }
            }
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            
            if (!prefsManager.isFullScreen()) {
                binding.homeContainer.setPadding(0, systemInsets.top, 0, systemInsets.bottom)
            } else {
                binding.homeContainer.setPadding(0, 0, 0, 0)
            }
            
            binding.appsRecycler.updatePadding(bottom = navInsets.bottom + (20 * resources.displayMetrics.density).toInt())
            
            insets
        }
    }

    private fun applySettings() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (prefsManager.isFullScreen()) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            binding.homeContainer.setPadding(0, 0, 0, 0)
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
            val insets = ViewCompat.getRootWindowInsets(binding.root)
            val systemInsets = insets?.getInsets(WindowInsetsCompat.Type.systemBars())
            if (systemInsets != null) {
                binding.homeContainer.setPadding(0, systemInsets.top, 0, systemInsets.bottom)
            }
        }

        binding.searchBar.visibility = if (prefsManager.isSearchBarEnabled()) View.VISIBLE else View.GONE
        
        val opacity = prefsManager.getDrawerOpacity()
        val bg = binding.bottomSheet.background
        if (bg is GradientDrawable) {
            bg.alpha = opacity
        }

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            applyBackgroundBlur()
        }
        
        updateDrawerModeUI(animate = false)
    }

    private fun updateDrawerModeUI(animate: Boolean = true) {
        val isVertical = prefsManager.isVerticalListModeEnabled()
        appsAdapter.setVerticalMode(isVertical)
        
        if (animate) {
            val transition = TransitionSet().apply {
                addTransition(Fade())
                duration = 250
                ordering = TransitionSet.ORDERING_TOGETHER
            }
            TransitionManager.beginDelayedTransition(binding.appsRecycler.parent as ViewGroup, transition)
        }
        
        binding.appsRecycler.layoutManager = if (isVertical) {
            LinearLayoutManager(this)
        } else {
            val glm = GridLayoutManager(this, 4)
            glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = appsAdapter.getSpanSize(position)
            }
            glm
        }
        binding.drawerTypeToggle.setImageResource(if (isVertical) R.drawable.ic_view_grid else R.drawable.ic_view_list)
    }

    private fun openNotificationPanel() {
        if (!prefsManager.isSwipeDownNotificationsEnabled()) return
        try {
            val statusBarService = getSystemService("statusbar")
            val statusBarManager: Class<*> = Class.forName("android.app.StatusBarManager")
            val expandMethod: Method = statusBarManager.getMethod("expandNotificationsPanel")
            expandMethod.isAccessible = true
            expandMethod.invoke(statusBarService)
        } catch (e: Exception) {
            try {
                val statusBarService = getSystemService("statusbar")
                val statusBarManager: Class<*> = Class.forName("android.app.StatusBarManager")
                val expandMethod: Method = statusBarManager.getMethod("expandSettingsPanel")
                expandMethod.isAccessible = true
                expandMethod.invoke(statusBarService)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        applySettings()
        loadLauncherWallpaper()
        fetchWeather()
        viewModel.loadApps()
        checkDefaultLauncher()
    }

    private fun checkDefaultLauncher() {
        if (!DefaultHomeActivity.isDismissedForSession && !isDefaultLauncher()) {
            val intent = Intent(this, DefaultHomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, 0)
        }
    }

    private fun isDefaultLauncher(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName == packageName
    }

    private fun applyBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurMagnitude = prefsManager.getBlurMagnitude().toFloat()
            val radius = (blurMagnitude * 0.5f).coerceAtLeast(0.1f)
            val blurEffect = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
            binding.wallpaperImageView.setRenderEffect(blurEffect)
        }
    }

    private fun showAppOptions(app: AppInfo, anchorView: View) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_app_options, null)
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.findViewById<ImageView>(R.id.dialogAppIcon).setImageDrawable(app.icon)
        dialogView.findViewById<TextView>(R.id.dialogAppName).text = app.label
        val favs = prefsManager.getFavorites().toMutableSet()
        val isFav = favs.contains(app.packageName)
        val txtFav = dialogView.findViewById<TextView>(R.id.txtFavorite)
        txtFav.text = if (isFav) "Remove from Favorites" else "Add to Favorites"
        dialogView.findViewById<View>(R.id.optionFavorite).setOnClickListener {
            dialog.dismiss()
            toggleFavorite(app)
        }
        dialogView.findViewById<View>(R.id.optionAppInfo).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = Uri.fromParts("package", app.packageName, null) })
        }
        dialogView.findViewById<View>(R.id.optionHide).setOnClickListener {
            dialog.dismiss()
            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Hide App")
                .setMessage("Do you want to hide ${app.label} from the drawer?")
                .setPositiveButton("Hide") { _, _ ->
                    val hidden = prefsManager.getHiddenApps().toMutableSet()
                    hidden.add(app.packageName)
                    prefsManager.saveHiddenApps(hidden)
                    viewModel.loadApps()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        dialogView.findViewById<View>(R.id.optionUninstall).setOnClickListener {
            dialog.dismiss()
            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Uninstall App")
                .setMessage("Do you want to uninstall ${app.label} from system?")
                .setPositiveButton("Uninstall") { _, _ ->
                    startActivity(Intent(Intent.ACTION_DELETE).apply { data = Uri.fromParts("package", app.packageName, null) })
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        dialog.show()
    }

    private fun checkLocationPermissions() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED) {
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { fetchWeather() }.launch(permissions)
        } else fetchWeather()
    }

    private fun fetchWeather() {
        try {
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }

            if (bestLocation != null) {
                updateWeatherWithLocation(bestLocation)
            } else {
                val provider = if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    LocationManager.NETWORK_PROVIDER
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    LocationManager.GPS_PROVIDER
                } else {
                    null
                }

                provider?.let {
                    locationManager.requestSingleUpdate(it, object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            updateWeatherWithLocation(location)
                        }
                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }, null)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun updateWeatherWithLocation(location: Location) {
        lifecycleScope.launch {
            val data = weatherManager.getWeatherData(location.latitude, location.longitude)
            data?.let { viewModel.updateWeather(it) }
        }
    }

    private fun updateWeatherUI(data: WeatherData) {
        binding.weatherTemp.text = "${data.temp.toInt()}°"
        binding.weatherCondition.text = data.condition
        binding.weatherDetails.text = "AQI ${data.aqi} • WIND ${data.windSpeed} km/h • HUM ${data.humidity}%"
        binding.quoteText.text = weatherManager.getMagazineQuote(data.condition)
    }

    private fun loadLauncherWallpaper() {
        val path = prefsManager.getWallpaper()
        if (!path.isNullOrEmpty()) {
            val file = File(path)
            if (file.exists()) {
                binding.wallpaperImageView.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                binding.wallpaperImageView.visibility = View.VISIBLE
                return
            }
        }
        binding.wallpaperImageView.setImageResource(R.drawable.one)
        binding.wallpaperImageView.visibility = View.VISIBLE
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.expandedOffset = (40 * resources.displayMetrics.density).toInt()
        bottomSheetBehavior.isGestureInsetBottomIgnored = true
        
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    applyBackgroundBlur()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        binding.homeContainer.setRenderEffect(null)
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (slideOffset > -0.98f) {
                        val radius = ((slideOffset + 1f) * 15f).coerceAtLeast(0.1f)
                        binding.homeContainer.setRenderEffect(RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP))
                    } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                        binding.homeContainer.setRenderEffect(null)
                    }
                }
            }
        })
    }

    private fun setupUI() {
        binding.searchBar.setOnClickListener { if (prefsManager.isSearchBarEnabled()) startActivity(Intent(this, SearchPanelActivity::class.java)) }
        binding.settingsIcon.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        
        binding.drawerTypeToggle.setOnClickListener {
            val isVertical = !prefsManager.isVerticalListModeEnabled()
            prefsManager.setVerticalListModeEnabled(isVertical)
            
            // Smoother icon rotation and list update
            binding.drawerTypeToggle.animate()
                .rotationBy(if (isVertical) 180f else -180f)
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator())
                .start()
                
            updateDrawerModeUI(animate = true)
        }
    }

    private fun setupGestures() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, vX: Float, vY: Float): Boolean {
                if (e1 == null) return false
                val dY = e2.y - e1.y
                val dX = e2.x - e1.x
                if (abs(dY) > abs(dX)) {
                    if (dY < 0 && prefsManager.isAppDrawerEnabled()) bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    else if (dY > 0) {
                        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        else openNotificationPanel()
                    }
                }
                return true
            }
            override fun onLongPress(e: MotionEvent) { if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) showHomeOptions() }
        })
        binding.homeContainer.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })
    }

    private fun showHomeOptions() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_home_options, null)
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog).setView(view).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.findViewById<View>(R.id.optionLauncherSettings).setOnClickListener { dialog.dismiss(); startActivity(Intent(this, SettingsActivity::class.java)) }
        view.findViewById<View>(R.id.optionSystemSettings).setOnClickListener { dialog.dismiss(); startActivity(Intent(Settings.ACTION_SETTINGS)) }
        dialog.show()
    }

    private fun setupRecyclerViews() {
        binding.favoritesRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.favoritesRecycler.adapter = favoritesAdapter
        
        binding.appsRecycler.itemAnimator = ExplodeItemAnimator()
        binding.appsRecycler.adapter = appsAdapter
    }

    private fun toggleFavorite(app: AppInfo) {
        val favs = prefsManager.getFavorites().toMutableSet()
        if (favs.contains(app.packageName)) favs.remove(app.packageName) else favs.add(app.packageName)
        prefsManager.saveFavorites(favs)
        viewModel.loadApps()
    }

    private fun launchApp(app: AppInfo, view: View) {
        val intent = packageManager.getLaunchIntentForPackage(app.packageName)
        if (intent != null) startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.width, view.height).toBundle())
    }

    private fun updateDateTime() {
        val cal = Calendar.getInstance()
        binding.timeText.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
        binding.dayText.text = SimpleDateFormat("EEEE", Locale.getDefault()).format(cal.time)
        binding.dateText.text = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(cal.time)
        updateBattery()
    }

    private fun updateBattery(intent: Intent? = null) {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val bat = intent ?: registerReceiver(null, filter)
        if (bat == null) return

        val level = bat.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = bat.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = bat.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val plugType = bat.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        
        val batteryPct = if (level != -1 && scale != -1) (level * 100 / scale) else -1
        
        if (batteryPct != -1) {
            binding.batteryText.text = "BATTERY $batteryPct%"
        }

        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        when {
            status == BatteryManager.BATTERY_STATUS_FULL -> {
                binding.chargingIcon.visibility = View.VISIBLE
                binding.chargingIcon.setImageResource(R.drawable.ic_charging_full)
                binding.chargingIcon.setColorFilter(Color.GREEN)
                stopChargingAnimation()
            }
            isCharging -> {
                binding.chargingIcon.visibility = View.VISIBLE
                binding.chargingIcon.setImageResource(R.drawable.ic_charging)
                if (plugType == BatteryManager.BATTERY_PLUGGED_AC) {
                    binding.chargingIcon.setColorFilter(Color.BLUE)
                } else {
                    binding.chargingIcon.setColorFilter(Color.YELLOW)
                }
                startChargingAnimation()
            }
            !isCharging && batteryPct != -1 && batteryPct < 15 -> {
                binding.chargingIcon.visibility = View.VISIBLE
                binding.chargingIcon.setImageResource(R.drawable.ic_battery_low)
                binding.chargingIcon.setColorFilter(Color.RED)
                stopChargingAnimation()
            }
            else -> {
                binding.chargingIcon.visibility = View.GONE
                stopChargingAnimation()
            }
        }
    }

    private fun startChargingAnimation() {
        if (chargingAnimator != null && chargingAnimator!!.isRunning) return
        
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.2f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.2f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1.0f, 0.6f)

        chargingAnimator = ObjectAnimator.ofPropertyValuesHolder(binding.chargingIcon, scaleX, scaleY, alpha).apply {
            duration = 1000
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun stopChargingAnimation() {
        chargingAnimator?.cancel()
        binding.chargingIcon.scaleX = 1.0f
        binding.chargingIcon.scaleY = 1.0f
        binding.chargingIcon.alpha = 1.0f
    }

    private fun startClockUpdate() { lifecycleScope.launch { while (isActive) { updateDateTime(); delay(60000) } } }
    private fun startWeatherUpdate() { lifecycleScope.launch { while (isActive) { fetchWeather(); delay(3600000) } } }
    
    override fun onStart() {
        super.onStart()
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        registerReceiver(packageReceiver, IntentFilter().apply { 
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package") 
        })
    }
    
    override fun onStop() { 
        super.onStop()
        unregisterReceiver(batteryReceiver)
        unregisterReceiver(packageReceiver)
    }
}
